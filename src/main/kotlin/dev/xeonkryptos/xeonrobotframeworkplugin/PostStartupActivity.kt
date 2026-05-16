package dev.xeonkryptos.xeonrobotframeworkplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.platform.lsp.api.LspServerManager
import dev.xeonkryptos.xeonrobotframeworkplugin.lsp.RobotLspServerSupportProvider

class PostStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        RobotListenerMgr.getInstance(project).initializeListeners()
    }
}
