package dev.xeonkryptos.xeonrobotframeworkplugin.lsp

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerManager
import com.intellij.platform.lsp.api.LspServerState
import com.intellij.util.FileContentUtilCore
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.eclipse.lsp4j.CreateFilesParams
import org.eclipse.lsp4j.FileCreate
import org.eclipse.lsp4j.SemanticTokensLegend
import org.eclipse.lsp4j.SemanticTokensParams
import org.eclipse.lsp4j.TextDocumentIdentifier
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

data class DecodedSemanticToken(val startOffset: Int, val endOffset: Int, val tokenType: String, val modifiers: Set<String>)

private data class CachedSemanticTokens(val modificationStamp: Long, val tokens: List<DecodedSemanticToken>)

@Service(Service.Level.PROJECT)
class RobotLspSemanticTokenService(private val project: Project, private val cs: CoroutineScope) {

    private val tokenCache = ConcurrentHashMap<String, CachedSemanticTokens>()

    @Volatile
    var legend: SemanticTokensLegend? = null

    fun getSemanticTokens(file: Path, text: CharSequence): List<DecodedSemanticToken>? {
        val lspServer = findLspServer() ?: return null
        var realPath = file.toRealPath().toString().replace("\\", "/").replace(":", "%3A")
        if (!realPath.startsWith('/')) realPath = "/$realPath"

        val fileUri = "file://$realPath"
        lspServer.sendNotification { server -> server.workspaceService.didCreateFiles(CreateFilesParams(listOf(FileCreate(fileUri)))) }
        return getSemanticTokens(TextDocumentIdentifier(fileUri), text, lspServer)
    }

    fun getSemanticTokens(file: VirtualFile, text: CharSequence): List<DecodedSemanticToken>? {
        val fileUri = file.url
        val modStamp = file.modificationStamp

        //val cached = tokenCache[fileUri]
        //if (cached != null && cached.modificationStamp == modStamp) {
        //    return cached.tokens
        //}

        val lspServer = findLspServer() ?: return null
        val documentIdentifier = lspServer.getDocumentIdentifier(file)
        val tokens = getSemanticTokens(documentIdentifier, text, lspServer) ?: return null
        //tokenCache[fileUri] = CachedSemanticTokens(modStamp, tokens)
        return tokens
    }

    private fun getSemanticTokens(docId: TextDocumentIdentifier, text: CharSequence, lspServer: LspServer): List<DecodedSemanticToken>? {
        val currentLegend = legend ?: return null
        val params = SemanticTokensParams(docId)

        val result = try {
            lspServer.sendRequestSync { server -> server.textDocumentService.semanticTokensFull(params) }
        } catch (e: ProcessCanceledException) {
            throw e
        } catch (e: Exception) {
            LOG.warn("Failed to get semantic tokens from LSP server", e)
            return null
        }
        result ?: return null
        return decodeSemanticTokens(result.data, text, currentLegend)
    }

    fun invalidateAllCaches() {
        tokenCache.clear()
    }

    fun reparseOpenFiles() {
        cs.launch {
            edtWriteAction {
                val openRobotFiles = FileEditorManager.getInstance(project).openFiles.filter {
                    it.fileType == RobotFeatureFileType.getInstance() || it.fileType == RobotResourceFileType.getInstance()
                }
                if (openRobotFiles.isNotEmpty()) {
                    FileContentUtilCore.reparseFiles(openRobotFiles)
                }
                DaemonCodeAnalyzer.getInstance(project).restart()
            }
        }
    }

    private fun findLspServer(): LspServer? {
        val servers = LspServerManager.getInstance(project).getServersForProvider(RobotLspServerSupportProvider::class.java)
        return servers.firstOrNull { it.state == LspServerState.Running }
    }

    private fun decodeSemanticTokens(data: List<Int>, text: CharSequence, legend: SemanticTokensLegend): List<DecodedSemanticToken> {
        if (data.isEmpty()) return emptyList()

        val lineStartOffsets = buildLineStartOffsets(text)
        val tokenTypes = legend.tokenTypes
        val tokenModifiers = legend.tokenModifiers
        val result = mutableListOf<DecodedSemanticToken>()

        var currentLine = 0
        var currentChar = 0

        var i = 0
        while (i + 4 < data.size) {
            val deltaLine = data[i]
            val deltaStartChar = data[i + 1]
            val length = data[i + 2]
            val tokenTypeIndex = data[i + 3]
            val modifierBits = data[i + 4]
            i += 5

            currentLine += deltaLine
            currentChar = if (deltaLine > 0) deltaStartChar else currentChar + deltaStartChar

            val startOffset = absoluteOffset(currentLine, currentChar, lineStartOffsets) ?: continue
            val endOffset = (startOffset + length).coerceAtMost(text.length)

            val typeName = if (tokenTypeIndex < tokenTypes.size) tokenTypes[tokenTypeIndex] else "unknown"
            val mods = mutableSetOf<String>()
            if (modifierBits != 0) {
                for (bit in tokenModifiers.indices) {
                    if ((modifierBits and (1 shl bit)) != 0 && bit < tokenModifiers.size) {
                        mods.add(tokenModifiers[bit])
                    }
                }
            }

            if (startOffset < endOffset) {
                result.add(DecodedSemanticToken(startOffset, endOffset, typeName, mods))
            }
        }

        return result
    }

    private fun buildLineStartOffsets(text: CharSequence): List<Int> {
        val starts = mutableListOf(0)
        var i = 0
        while (i < text.length) {
            when (text[i]) {
                '\r' -> {
                    if (i + 1 < text.length && text[i + 1] == '\n') i++
                    starts.add(i + 1)
                }

                '\n' -> starts.add(i + 1)
            }
            i++
        }
        return starts
    }

    private fun absoluteOffset(line: Int, char: Int, lineStarts: List<Int>): Int? {
        if (line < 0 || line >= lineStarts.size) return null
        return lineStarts[line] + char
    }

    companion object {
        private val LOG = Logger.getInstance(RobotLspSemanticTokenService::class.java)

        fun getInstance(project: Project): RobotLspSemanticTokenService {
            return project.getService(RobotLspSemanticTokenService::class.java)
        }
    }
}
