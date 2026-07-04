package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.ParameterizedCachedValue
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.io.awaitExit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@Service(Service.Level.PROJECT)
class RobotVersionProvider(private val project: Project, private val cs: CoroutineScope) {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): RobotVersionProvider = project.service()

        private val ROBOT_FRAMEWORK_VERSION_REGEX = Regex("""(\d+)\.(\d+)\.(\d+)""")

        val ROBOT_VERSION_CACHE_KEY: Key<ParameterizedCachedValue<RobotVersion?, String>> = Key.create("ROBOT_VERSION_KEY")
    }

    fun getRobotVersion(sourceElement: PsiElement): RobotVersion? {
        val sdk = RobotLocalProcessExecutor.findPythonSdk(sourceElement)
        sdk?.homePath ?: return null
        val robotVersion = CachedValuesManager.getManager(project).getParameterizedCachedValue(sdk, ROBOT_VERSION_CACHE_KEY, { homePath ->
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
            val matchResult = ROBOT_FRAMEWORK_VERSION_REGEX.find(consoleOutput) ?: return@getParameterizedCachedValue null
            val (major, minor, patch) = matchResult.destructured
            return@getParameterizedCachedValue CachedValueProvider.Result.createSingleDependency(RobotVersion(major.toInt(), minor.toInt(), patch.toInt()), PsiModificationTracker.MODIFICATION_COUNT)
        }, false, sdk.homePath)
        return robotVersion
    }

    data class RobotVersion(val major: Int, val minor: Int, val patch: Int) {

        fun supports(version: RobotVersion): Boolean {
            return major > version.major || major == version.major && minor > version.minor || major == version.major && minor == version.minor && patch >= version.patch
        }
    }
}
