package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Service(Service.Level.PROJECT)
class RobotVersionUpdaterService(private val project: Project, private val cs: CoroutineScope) : Disposable {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): RobotVersionUpdaterService = project.service()
    }

    private var versionUpdaterJob: Job? = null

    fun startVersionUpdater(interval: Duration = 1000L.milliseconds) {
         versionUpdaterJob = cs.startInfiniteScheduler(interval) {
            val robotVersionProvider = RobotVersionProvider.getInstance(project)
            robotVersionProvider.computeRobotVersion()
        }
    }

    override fun dispose() {
        versionUpdaterJob?.cancel()
    }
}

fun CoroutineScope.startInfiniteScheduler(interval: Duration = 1000L.milliseconds, task: suspend () -> Unit): Job {
    return launch {
        while (isActive) {
            task()
            delay(interval)
        }
    }
}
