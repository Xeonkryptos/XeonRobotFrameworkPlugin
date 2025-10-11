package dev.xeonkryptos.xeonrobotframeworkplugin.ide

import com.intellij.icons.AllIcons
import com.intellij.ide.IconProvider
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.roots.FileIndexFacade
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.jetbrains.python.psi.PyUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames
import javax.swing.Icon

class RobotDirectoryIconProvider : IconProvider() {

    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (element is PsiDirectory) {
            // Preserve original icons for excluded directories and source roots
            if (isSpecialDirectory(element)) return null
            if (element.findFile(RobotNames.INIT_DOT_ROBOT) != null) {
                return AllIcons.Nodes.Package
            }
        }
        return null
    }

    private fun isSpecialDirectory(directory: PsiDirectory): Boolean {
        val vFile = directory.virtualFile
        if (FileIndexFacade.getInstance(directory.project).isExcludedFile(vFile)) {
            return true
        }
        val module = ModuleUtilCore.findModuleForPsiElement(directory)
        return module == null || PyUtil.getSourceRoots(module).contains(vFile)
    }
}
