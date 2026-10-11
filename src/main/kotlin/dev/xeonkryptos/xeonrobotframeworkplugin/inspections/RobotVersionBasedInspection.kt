package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.Companion.getInstance
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion
import java.util.concurrent.TimeUnit

abstract class RobotVersionBasedInspection : LocalInspectionTool() {

    protected abstract val minimumRobotVersion: RobotVersion

    protected abstract val negativeRobotVersionCheck: Boolean

    override fun isAvailableForFile(file: PsiFile): Boolean {
        var robotVersion = file.getUserData<TimedRobotVersion>(ROBOT_VERSION_KEY)
        val minimumRobotVersion = this.minimumRobotVersion
        if (robotVersion == null || robotVersion.isExpired) {
            robotVersion = getRobotVersion(file)
        }
        if (robotVersion != null) {
            file.putUserData<TimedRobotVersion>(ROBOT_VERSION_KEY, robotVersion)
            return robotVersion.robotVersion.supports(minimumRobotVersion) && !negativeRobotVersionCheck
        }
        return false
    }

    override fun inspectionStarted(session: LocalInspectionToolSession, isOnTheFly: Boolean) {
        val file = session.file
        var foundRobotVersion = file.getUserData<TimedRobotVersion>(ROBOT_VERSION_KEY)
        if (foundRobotVersion == null || foundRobotVersion.isExpired) {
            foundRobotVersion = getRobotVersion(file)
        }
        session.putUserData<TimedRobotVersion>(ROBOT_VERSION_KEY, foundRobotVersion)
    }

    private fun getRobotVersion(file: PsiFile): TimedRobotVersion? {
        val project = file.project
        val robotVersion = getInstance(project).robotVersion ?: return null
        return TimedRobotVersion(robotVersion)
    }

    protected fun getRobotVersion(session: LocalInspectionToolSession): RobotVersion? = session.getUserData(ROBOT_VERSION_KEY)?.robotVersion

    @JvmRecord
    internal data class TimedRobotVersion(val robotVersion: RobotVersion, val timestamp: Long) {

        constructor(robotVersion: RobotVersion) : this(robotVersion, System.nanoTime())

        val isExpired: Boolean
            get() {
                val currentTime = System.nanoTime()
                val expirationTime = TimeUnit.NANOSECONDS.toMillis(timestamp) + EXPIRATION_MILLIS
                return currentTime > expirationTime
            }

        companion object {
            private val EXPIRATION_MILLIS = TimeUnit.MINUTES.toMillis(5)
        }
    }

    companion object {
        private val ROBOT_VERSION_KEY = Key.create<TimedRobotVersion?>("ROBOT_VERSION_KEY")
    }
}
