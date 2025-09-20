package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable

class RobotInvalidKeywordOnlyMarkerAnnotation : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is RobotVariable || element.getVariableName() != null) {
            return
        }
        val settingsParent = PsiTreeUtil.getParentOfType(element, RobotLocalArgumentsSetting::class.java)
        if (settingsParent == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.variable.blank.invalid"))
                .highlightType(ProblemHighlightType.GENERIC_ERROR)
                .range(element)
                .create()
        }
    }
}
