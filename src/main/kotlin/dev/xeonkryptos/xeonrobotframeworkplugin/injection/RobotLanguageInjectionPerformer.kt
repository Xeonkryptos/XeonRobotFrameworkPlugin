package dev.xeonkryptos.xeonrobotframeworkplugin.injection

import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionPerformer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.jetbrains.python.ast.findChildrenByClass
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable

class RobotLanguageInjectionPerformer : LanguageInjectionPerformer {

    companion object {
        private const val ROBOT_VARIABLE_PLACEHOLDER = "__robot_var__"
    }

    override fun isPrimary(): Boolean = true

    override fun performInjection(registrar: MultiHostRegistrar, injection: Injection, context: PsiElement): Boolean {
        val language = injection.injectedLanguage ?: return false
        if (context !is PsiLanguageInjectionHost || !context.isValidHost) return false

        val manipulator = ElementManipulators.getManipulator<PsiLanguageInjectionHost>(context)
        val rangeInElement = manipulator.getRangeInElement(context)

        registrar.startInjecting(language)
        if (context.children.size > 1) {
            @Suppress("UnstableApiUsage")
            val variables = context.findChildrenByClass(RobotVariable::class.java)

            val firstVariableAtStartOfConditionRange = variables.first().textRangeInParent
            var startOffset = if (firstVariableAtStartOfConditionRange.startOffset != rangeInElement.startOffset) {
                registrar.addPlace(injection.prefix, null, context, TextRange(rangeInElement.startOffset, firstVariableAtStartOfConditionRange.startOffset))
                firstVariableAtStartOfConditionRange.endOffset
            } else {
                val nextVariable = variables.elementAtOrNull(1)
                val suffix: String?
                val endOffset: Int
                if (nextVariable != null) {
                    suffix = null
                    endOffset = nextVariable.textRangeInParent.startOffset
                } else {
                    suffix = injection.suffix
                    endOffset = rangeInElement.endOffset
                }
                registrar.addPlace("${injection.prefix}${ROBOT_VARIABLE_PLACEHOLDER}", suffix, context, TextRange(firstVariableAtStartOfConditionRange.endOffset, endOffset))
                endOffset
            }

            for (variable in variables.drop(1)) {
                val variableTextRange = variable.textRangeInParent
                val suffix = if (variableTextRange.endOffset == rangeInElement.endOffset) injection.suffix else null
                registrar.addPlace(ROBOT_VARIABLE_PLACEHOLDER, suffix, context, TextRange(startOffset, variableTextRange.startOffset))
                startOffset = variableTextRange.endOffset
            }

            if (startOffset < rangeInElement.endOffset) {
                registrar.addPlace(ROBOT_VARIABLE_PLACEHOLDER, injection.suffix, context, TextRange(startOffset, rangeInElement.endOffset))
            }
        } else {
            registrar.addPlace(injection.prefix, injection.suffix, context, rangeInElement)
        }
        registrar.doneInjecting()
        return true
    }
}
