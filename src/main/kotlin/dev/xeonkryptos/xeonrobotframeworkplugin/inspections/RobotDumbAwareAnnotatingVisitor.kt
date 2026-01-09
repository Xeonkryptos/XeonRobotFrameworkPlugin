package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotLocalArgumentsSettingAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotUserKeywordStatementAnnotator

class RobotDumbAwareAnnotatingVisitor : RobotAnnotatingVisitor(), DumbAware {

    private val annotators = arrayOf(
        RobotUserKeywordStatementAnnotator(), RobotLocalArgumentsSettingAnnotator()
    )

    override fun annotate(psiElement: PsiElement, holder: AnnotationHolder) {
        RobotAnnotatingVisitorExecutor.runAnnotators(psiElement, holder, annotators)
    }
}
