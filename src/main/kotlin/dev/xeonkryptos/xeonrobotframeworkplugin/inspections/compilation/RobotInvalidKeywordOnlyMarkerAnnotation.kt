package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.util.PsiTreeUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.EmptyVariableDetector

class RobotInvalidKeywordOnlyMarkerAnnotation : RobotAnnotator() {

    override fun visitVariable(element: RobotVariable) {
        if (element.variableName != null) {
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
}
