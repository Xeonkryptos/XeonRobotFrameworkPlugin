package dev.xeonkryptos.xeonrobotframeworkplugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.*
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.highlight.ReassignedRobotVariableAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.highlight.RobotVariableDefinitionNotFoundAnnotator

open class RobotAnnotatingVisitor : Annotator {

    private val annotators = arrayOf(RobotArgumentAnnotator(),
        RobotTemplateParameterAnnotator(),
        ReassignedRobotVariableAnnotator(),
        RobotVariableDefinitionNotFoundAnnotator(),
        RobotInvalidKeywordOnlyMarkerAnnotator(),
        RobotMissingMandatoryTemplateArgumentsAnnotator(),
        RobotParameterAnnotator(),
        KeywordCallWithInvalidParameterOrderAnnotator(),
        RobotUnresolvedKeywordAnnotator(),
        RobotMissingMandatoryKeywordParametersAnnotator(),
        RobotImportAnnotator())

    override fun annotate(psiElement: PsiElement, holder: AnnotationHolder) {
        RobotAnnotatingVisitorExecutor.runAnnotators(psiElement, holder, annotators)
    }
}
