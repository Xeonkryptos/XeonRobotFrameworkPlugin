package dev.xeonkryptos.xeonrobotframeworkplugin.execution

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Urls
import kotlin.math.max

class RobotSMTestLocator : SMTestLocator {

    companion object {
        @JvmStatic
        fun createLocationUrl(filePath: String, lineNumber: Int): String {
            return "robotcode:///${filePath}?line=${lineNumber}"
        }
    }

    override fun getLocation(
        protocol: String, path: String, project: Project, scope: GlobalSearchScope
    ): MutableList<Location<PsiElement>> {
        val uri = Urls.parse("file://$path", true)
        if (uri != null) {
            val line = uri.parameters?.drop(1)?.split("&")?.firstOrNull { it.startsWith("line=") }?.substring(5)?.toIntOrNull()?.minus(1)

            LocalFileSystem.getInstance().findFileByPath(uri.path)?.let { virtualFile ->
                PsiManager.getInstance(project).findFile(virtualFile)?.let { psiFile ->
                    PsiDocumentManager.getInstance(project).getDocument(psiFile)?.let { doc ->
                        val offset = doc.getLineStartOffset(max(line ?: 0, 0))
                        psiFile.findElementAt(offset)
                    }
                }
            }?.let { return mutableListOf(PsiLocation(it)) }
        }
        return mutableListOf()
    }

}
