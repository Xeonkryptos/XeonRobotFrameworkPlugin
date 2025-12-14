package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotCallArgumentsCollector
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeyUtils

class KeywordCallWithInvalidParameterOrderAnnotator : Annotator, DumbAware {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is RobotKeywordCall) return

        val robotCallArgumentsCollector = RobotCallArgumentsCollector()
        element.acceptChildren(robotCallArgumentsCollector)

        var parameterFound = false
        for (argument in robotCallArgumentsCollector.arguments) {
            if (argument is RobotParameter) {
                parameterFound = argument.getUserData(KeyUtils.HANDLED_AS_SIMPLE_ARGUMENT_KEY)?.not() ?: false
            } else if (parameterFound) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.keyword.arguments.mixed"))
                    .highlightType(ProblemHighlightType.GENERIC_ERROR)
                    .range(argument)
                    .create()
            }
        }
    }
}
