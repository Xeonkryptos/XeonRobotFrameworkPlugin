package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.jetbrains.python.sdk.PythonSdkUtil
import java.io.File
import java.util.stream.Collectors
import java.util.stream.Stream

class RobotLocalProcessExecutor {

    companion object {

        @JvmStatic
        fun findPythonSdk(sourceElement: PsiElement): Sdk? {
            var sdk = PythonSdkUtil.findPythonSdk(sourceElement)
            if (sdk == null) {
                sdk = ProjectRootManager.getInstance(sourceElement.project).projectSdk
                if (sdk != null && !PythonSdkUtil.isPythonSdk(sdk)) {
                    sdk = null
                }
            }
            return sdk
        }

        @JvmStatic
        fun setupPythonPathForModule(processBuilder: ProcessBuilder, module: Module?) {
            if (module == null) return

            val sourceRoots = ModuleRootManager.getInstance(module).sourceRoots
            val contentRoots = ProjectRootManager.getInstance(module.project).contentRoots
            val pythonPath =
                Stream.concat(Stream.of(*sourceRoots), Stream.of(*contentRoots))
                    .map(VfsUtilCore::virtualToIoFile)
                    .map(File::getAbsolutePath)
                    .collect(Collectors.joining(File.pathSeparator))

            val env = processBuilder.environment()
            env["PYTHONPATH"] = pythonPath
            env["PYTHONIOENCODING"] = "utf-8"
        }
    }
}
