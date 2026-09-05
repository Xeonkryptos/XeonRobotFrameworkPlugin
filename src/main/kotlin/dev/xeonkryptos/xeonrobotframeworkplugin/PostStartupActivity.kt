package dev.xeonkryptos.xeonrobotframeworkplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.ProjectActivity
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionUpdaterService
import kotlin.time.Duration.Companion.minutes

class PostStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        RobotListenerMgr.getInstance(project).initializeListeners()
        ProjectRootManager.getInstance(project).projectSdk

        RobotVersionUpdaterService.getInstance(project).startVersionUpdater(1.minutes)
    }
}
