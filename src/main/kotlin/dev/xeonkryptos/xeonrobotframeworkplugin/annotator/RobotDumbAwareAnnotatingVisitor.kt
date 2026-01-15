package dev.xeonkryptos.xeonrobotframeworkplugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.DuplicatedDefinedForLoopParameterSettingsAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.ForLoopParameterRestrictionAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.InvalidlyPlacedBuiltInKeywordCallsAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.InvalidlyPlacedConditionalKeywordsAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotInZipVariableSourceTypeAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotLocalArgumentsSettingAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation.RobotUserKeywordStatementAnnotator

class RobotDumbAwareAnnotatingVisitor : RobotAnnotatingVisitor(), DumbAware {

    private val annotators = arrayOf(
        RobotUserKeywordStatementAnnotator(),
        RobotLocalArgumentsSettingAnnotator(),
        DuplicatedDefinedForLoopParameterSettingsAnnotator(),
        ForLoopParameterRestrictionAnnotator(),
        RobotInZipVariableSourceTypeAnnotator(),
        InvalidlyPlacedConditionalKeywordsAnnotator(),
        InvalidlyPlacedBuiltInKeywordCallsAnnotator()
    )

    override fun annotate(psiElement: PsiElement, holder: AnnotationHolder) {
        RobotAnnotatingVisitorExecutor.runAnnotators(psiElement, holder, annotators)
    }
}
