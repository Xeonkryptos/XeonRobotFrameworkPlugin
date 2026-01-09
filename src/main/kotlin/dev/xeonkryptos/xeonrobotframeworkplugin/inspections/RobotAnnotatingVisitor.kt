package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.KeywordCallWithInvalidParameterOrderAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotArgumentAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotImportAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotInvalidKeywordOnlyMarkerAnnotation
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotMissingMandatoryKeywordParametersAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotMissingMandatoryTemplateArgumentsAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotParameterAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotTemplateParameterAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation.RobotUnresolvedKeywordAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.highlight.ReassignedRobotVariableAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.highlight.RobotVariableDefinitionNotFoundAnnotator

open class RobotAnnotatingVisitor : Annotator {

    private val annotators = arrayOf(
        RobotArgumentAnnotator(),
        RobotTemplateParameterAnnotator(),
        ReassignedRobotVariableAnnotator(),
        RobotVariableDefinitionNotFoundAnnotator(),
        RobotInvalidKeywordOnlyMarkerAnnotation(),
        RobotMissingMandatoryTemplateArgumentsAnnotator(),
        RobotParameterAnnotator(),
        // KeywordCallWithInvalidParameterOrderAnnotator is technically dumb-aware, but it depends on the evaluation result of RobotParameterAnnotator
        // which isn't dumb-aware itself
        KeywordCallWithInvalidParameterOrderAnnotator(),
        RobotUnresolvedKeywordAnnotator(),
        RobotMissingMandatoryKeywordParametersAnnotator(),
        RobotImportAnnotator()
    )

    override fun annotate(psiElement: PsiElement, holder: AnnotationHolder) {
        RobotAnnotatingVisitorExecutor.runAnnotators(psiElement, holder, annotators)
    }
}
