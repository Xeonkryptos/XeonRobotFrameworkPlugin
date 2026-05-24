package dev.xeonkryptos.xeonrobotframeworkplugin.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspCommunicationChannel
import com.intellij.platform.lsp.api.LspServerListener
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.customization.LspCodeActionsSupport
import com.intellij.platform.lsp.api.customization.LspCommandsSupport
import com.intellij.platform.lsp.api.customization.LspCompletionSupport
import com.intellij.platform.lsp.api.customization.LspDiagnosticsSupport
import com.intellij.platform.lsp.api.customization.LspDocumentColorSupport
import com.intellij.platform.lsp.api.customization.LspDocumentLinkSupport
import com.intellij.platform.lsp.api.customization.LspFindReferencesSupport
import com.intellij.platform.lsp.api.customization.LspFormattingSupport
import com.intellij.platform.lsp.api.customization.LspSemanticTokensSupport
import com.jetbrains.python.sdk.pythonSdk
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes.RobotFeatureFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes.RobotResourceFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.util.BundleUtil

class RobotLspServerSupportProvider : LspServerSupportProvider {

    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (file.fileType == RobotFeatureFileType.getInstance() || file.fileType == RobotResourceFileType.getInstance()) {
            serverStarter.ensureServerStarted(RobotLspServerDescriptor(project))
        }
    }

    class RobotLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, RobotBundle.message("framework.name")) {

        override val lspCommunicationChannel: LspCommunicationChannel = LspCommunicationChannel.StdIO

        override val lspCodeActionsSupport: LspCodeActionsSupport? = null
        override val lspCommandsSupport: LspCommandsSupport? = null
        override val lspCompletionSupport: LspCompletionSupport? = null
        override val lspDiagnosticsSupport: LspDiagnosticsSupport? = null
        override val lspDocumentColorSupport: LspDocumentColorSupport? = null
        override val lspDocumentLinkSupport: LspDocumentLinkSupport? = null
        override val lspFindReferencesSupport: LspFindReferencesSupport? = null
        override val lspFormattingSupport: LspFormattingSupport? = null

        override val lspGoToDefinitionSupport: Boolean = false
        override val lspGoToTypeDefinitionSupport: Boolean = false
        override val lspHoverSupport: Boolean = false
        override val lspSemanticTokensSupport: LspSemanticTokensSupport? = null

        override val lspServerListener: LspServerListener? = null

        override fun isSupportedFile(file: VirtualFile) = file.fileType == RobotFeatureFileType.getInstance() || file.fileType == RobotResourceFileType.getInstance()

        override fun createCommandLine(): GeneralCommandLine {
            val pythonSdk = project.pythonSdk
            if (pythonSdk == null) {
                Notification("Robot", RobotBundle.message("notification.group.missing.valid.python.interpreter.content"), NotificationType.ERROR).notify(project)
                throw IllegalArgumentException("PythonSDK is not defined yet.")
            }
            val pythonSdkHomePath = pythonSdk.homePath
            if (pythonSdkHomePath == null) {
                Notification("Robot", RobotBundle.message("notification.group.missing.valid.python.interpreter.content"), NotificationType.ERROR).notify(project)
                throw IllegalArgumentException("Home path of PythonSDK is unknown")
            }
            return GeneralCommandLine().withExePath(pythonSdkHomePath).withParameters(BundleUtil.ROBOTCODE_DIR.toString(), "language-server", "--stdio")
        }
    }
}
