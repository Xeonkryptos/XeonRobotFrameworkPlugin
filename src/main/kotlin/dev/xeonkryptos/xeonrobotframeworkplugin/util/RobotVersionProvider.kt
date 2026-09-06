package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.io.awaitExit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.Volatile

@Service(Service.Level.PROJECT)
class RobotVersionProvider(private val project: Project, private val cs: CoroutineScope) {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): RobotVersionProvider = project.service()

        private val ROBOT_FRAMEWORK_VERSION_REGEX = Regex("""(\d+)\.(\d+)\.(\d+)""")
    }

    @Volatile
    var robotVersion: RobotVersion? = null
        private set

    fun computeRobotVersion() {
        val sdk = RobotLocalProcessExecutor.findPythonSdk(project)
        val homePath = sdk?.homePath ?: return
        val requestRobotVersion = cs.async(Dispatchers.IO, CoroutineStart.LAZY) {
            val processArguments = mutableListOf(homePath, "-m", "robot", "--version")
            val processBuilder = ProcessBuilder(processArguments)
            processBuilder.redirectErrorStream()
            val process = processBuilder.start()

            val consoleOutput = async(Dispatchers.IO) { process.inputStream.readAllBytes().decodeToString() }
            process.awaitExit()
            return@async consoleOutput.await()
        }

        val consoleOutput = runBlocking { requestRobotVersion.await() }
        val matchResult = ROBOT_FRAMEWORK_VERSION_REGEX.find(consoleOutput) ?: return
        val (major, minor, patch) = matchResult.destructured
        robotVersion = RobotVersion(major.toInt(), minor.toInt(), patch.toInt())
    }

    data class RobotVersion(val major: Int, val minor: Int, val patch: Int) {

        fun supports(version: RobotVersion): Boolean {
            return major > version.major || major == version.major && minor > version.minor || major == version.major && minor == version.minor && patch >= version.patch
        }
    }
}
