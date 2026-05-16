package dev.xeonkryptos.xeonrobotframeworkplugin.lsp

import com.intellij.lexer.LexerBase
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.io.delete
import com.intellij.util.io.write
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import java.nio.file.Files
import java.nio.file.Path

class RobotLspLexer(private val project: Project?, private val file: VirtualFile?) : LexerBase() {

    private var buffer: CharSequence = ""
    private var bufferStart: Int = 0
    private var bufferEnd: Int = 0
    private var tokens: List<MappedToken>? = null
    private var index: Int = -1
    private var initialState: Int = 0

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferStart = startOffset
        this.bufferEnd = endOffset
        this.initialState = initialState
        this.index = 0
        this.tokens = null

        if (buffer.isNotEmpty()) {
            val service = project?.service<RobotLspSemanticTokenService>()

            val parsingContextFile = RobotLspLexerFileContext.currentFile.get()
            val semanticTokens: List<DecodedSemanticToken>? = if (service == null) null
            else if (file != null && file !is LightVirtualFile) service.getSemanticTokens(file, buffer)
            else if (parsingContextFile != null) service.getSemanticTokens(parsingContextFile, buffer)
            else retrieveSemanticTokensWithTempFile(service)

            tokens = if (semanticTokens != null) {
                RobotLspTokenMapper.mapAndFillGaps(semanticTokens, buffer, bufferStart, bufferEnd)
            } else {
                listOf(MappedToken(RobotTypes.COMMENT, bufferStart, bufferEnd - bufferStart))
            }
        }
    }

    override fun advance() {
        index++
    }

    private fun retrieveSemanticTokensWithTempFile(service: RobotLspSemanticTokenService): List<DecodedSemanticToken>? {
        var tempFile: Path? = null
        try {
            tempFile = Files.createTempFile("Dummy", ".robot")
            tempFile.write(buffer)
            return service.getSemanticTokens(tempFile, buffer)
        } finally {
            tempFile?.delete()
        }
    }

    override fun getState(): Int = initialState

    override fun getTokenType(): IElementType? = tokens?.getOrNull(index)?.elementType

    override fun getTokenStart(): Int = tokens?.getOrNull(index)?.startOffset ?: bufferEnd

    override fun getTokenEnd(): Int {
        val token = tokens?.getOrNull(index) ?: return bufferEnd
        return token.startOffset + token.length
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd
}
