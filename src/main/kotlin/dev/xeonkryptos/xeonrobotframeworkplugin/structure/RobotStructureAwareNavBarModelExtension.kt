package dev.xeonkryptos.xeonrobotframeworkplugin.structure

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import com.intellij.psi.impl.PsiElementBase
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import javax.swing.Icon

class RobotStructureAwareNavBarModelExtension() : StructureAwareNavBarModelExtension() {

    override val language: Language = RobotLanguage.INSTANCE

    override fun getPresentableText(element: Any?): String? {
        if (element is PsiElementBase && element.presentation != null) {
            return element.presentation?.presentableText
        }
        return null
    }

    override fun getIcon(element: Any?): Icon? {
        if (element is PsiElementBase && element.presentation != null) {
            return element.presentation?.getIcon(false)
        }
        return null
    }
}
