package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker

@Service(Service.Level.PROJECT)
class RobotVersionProvider {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): RobotVersionProvider = project.service()

        private val ROBOT_FRAMEWORK_VERSION_REGEX = Regex("""(\d+)\.(\d+)\.(\d+)""")
    }

    fun getRobotVersion(sourceElement: PsiElement): RobotVersion? {
        val sdk = RobotLocalProcessExecutor.findPythonSdk(sourceElement)
        if (sdk == null || sdk.homePath == null) return null

        return CachedValuesManager.getCachedValue(sourceElement) {
            val processArguments = mutableListOf(sdk.homePath!!, "-m", "robot", "--version")

            val processBuilder = ProcessBuilder(processArguments)

            val module = ModuleUtilCore.findModuleForPsiElement(sourceElement)
            RobotLocalProcessExecutor.setupPythonPathForModule(processBuilder, module)

            var robotVersion: RobotVersion? = null
            val process = processBuilder.start()
            process.waitFor()
            val consoleOutput = process.inputStream.readAllBytes().decodeToString()
            val matchResult = ROBOT_FRAMEWORK_VERSION_REGEX.find(consoleOutput)
            if (matchResult != null) {
                val (major, minor, patch) = matchResult.destructured
                robotVersion = RobotVersion(major.toInt(), minor.toInt(), patch.toInt())
            }
            return@getCachedValue CachedValueProvider.Result.createSingleDependency(robotVersion, PsiModificationTracker.MODIFICATION_COUNT)
        }
    }

    data class RobotVersion(val major: Int, val minor: Int, val patch: Int) {

        fun supports(version: RobotVersion): Boolean {
            return this.major >= version.major && this.minor >= version.minor && this.patch >= version.patch
        }
    }
}
