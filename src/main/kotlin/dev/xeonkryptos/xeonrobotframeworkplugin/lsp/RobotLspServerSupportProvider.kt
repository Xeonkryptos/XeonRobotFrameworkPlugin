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
import com.jetbrains.python.sdk.pythonSdk
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes.RobotFeatureFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes.RobotResourceFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.util.BundleUtil

// LSP Server will be required in a future version of the plugin, to evaluate python code dynamically.
@Suppress("unused")
class RobotLspServerSupportProvider : LspServerSupportProvider {

    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (file.fileType == RobotFeatureFileType.getInstance() || file.fileType == RobotResourceFileType.getInstance()) {
            serverStarter.ensureServerStarted(RobotLspServerDescriptor(project))
        }
    }

    class RobotLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, RobotBundle.message("framework.name")) {

        override val lspCommunicationChannel: LspCommunicationChannel = LspCommunicationChannel.StdIO

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
