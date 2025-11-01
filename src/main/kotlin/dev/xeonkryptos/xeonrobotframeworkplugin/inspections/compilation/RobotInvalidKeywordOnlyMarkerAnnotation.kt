package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.EmptyVariableDetector
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable

class RobotInvalidKeywordOnlyMarkerAnnotation : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is RobotVariable || element.getVariableName() != null) {
            return
        }
        val settingsParent = PsiTreeUtil.getParentOfType(element, RobotLocalArgumentsSetting::class.java)
        if (settingsParent == null) {
            val detector = EmptyVariableDetector()
            element.accept(detector)
            if (detector.emptyVariable) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.variable.blank.invalid"))
                    .highlightType(ProblemHighlightType.GENERIC_ERROR)
                    .range(element)
                    .create()
            }
        }
    }
}
