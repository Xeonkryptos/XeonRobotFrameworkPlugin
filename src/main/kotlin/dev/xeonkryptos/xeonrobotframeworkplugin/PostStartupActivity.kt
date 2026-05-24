package dev.xeonkryptos.xeonrobotframeworkplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class PostStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        RobotListenerMgr.getInstance(project).initializeListeners()
    }
}
