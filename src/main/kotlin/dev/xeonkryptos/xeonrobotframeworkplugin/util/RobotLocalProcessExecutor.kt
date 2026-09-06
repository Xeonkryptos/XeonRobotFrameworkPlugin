package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.jetbrains.python.sdk.PythonSdkUtil

object RobotLocalProcessExecutor {
    @JvmStatic
    fun findPythonSdk(project: Project): Sdk? {
        val sdk = ProjectRootManager.getInstance(project).projectSdk
        return if (sdk != null && !PythonSdkUtil.isPythonSdk(sdk)) null else sdk
    }
}
