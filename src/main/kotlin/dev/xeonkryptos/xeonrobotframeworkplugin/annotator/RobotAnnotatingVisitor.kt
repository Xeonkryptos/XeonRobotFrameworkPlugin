package dev.xeonkryptos.xeonrobotframeworkplugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.KeywordCallWithInvalidParameterOrderAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotArgumentAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotImportAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotInvalidKeywordOnlyMarkerAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotMissingMandatoryKeywordParametersAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotMissingMandatoryTemplateArgumentsAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotParameterAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotTemplateParameterAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotUnresolvedKeywordAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.highlight.ReassignedRobotVariableAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.highlight.RobotVariableDefinitionNotFoundAnnotator

open class RobotAnnotatingVisitor : Annotator {

    private val annotators = arrayOf(
        RobotArgumentAnnotator(),
        RobotTemplateParameterAnnotator(),
        ReassignedRobotVariableAnnotator(),
        RobotVariableDefinitionNotFoundAnnotator(),
        RobotInvalidKeywordOnlyMarkerAnnotator(),
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
