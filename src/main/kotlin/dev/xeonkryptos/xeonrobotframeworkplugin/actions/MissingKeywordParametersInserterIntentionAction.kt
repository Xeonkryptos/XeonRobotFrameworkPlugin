package dev.xeonkryptos.xeonrobotframeworkplugin.actions

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.TokenType
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

sealed class MissingKeywordParametersInserterIntentionAction(private val familyName: String) : PsiElementBaseIntentionAction() {

    init {
        text = familyName
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.node.elementType === RobotTypes.EOL || element.node.elementType === TokenType.WHITE_SPACE || element.node.elementType === RobotTypes.KEYWORD_NAME || element.node.elementType === RobotTypes.KEYWORD_LIBRARY_NAME) {
            val keywordCall = element.parentOfType<RobotKeywordCall>(false) ?: return false
            if (keywordCall.lastChild?.node?.elementType !== RobotTypes.EOL) return false

            return keywordCall.computeMissingParameters().any { !it.isPositionalOnly && !it.isPositionalContainer && !it.isKeywordContainer }
        }
        return false
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val keywordCall = element.parentOfType<RobotKeywordCall>(false) ?: return
        val missingParameters = keywordCall.computeMissingParameters()

        val elementGenerator = RobotElementGenerator.getInstance(project)
        val parserFacade = PsiParserFacade.getInstance(project)
        missingParameters.filter { !it.isPositionalOnly && !it.isPositionalContainer && !it.isKeywordContainer }.forEach { parameter ->
            val newParameter = computeNewParameter(parameter, elementGenerator)
            val eolNode = keywordCall.lastChild ?: return

            val addedParameterElement = keywordCall.addBefore(newParameter, eolNode)
            val superSpaceElement = parserFacade.createWhiteSpaceFromText(GlobalConstants.SUPER_SPACE)
            keywordCall.addBefore(superSpaceElement, addedParameterElement)
        }
    }

    abstract fun computeNewParameter(parameter: DefinedParameter, elementGenerator: RobotElementGenerator): RobotParameter

    override fun getFamilyName(): @IntentionFamilyName String = familyName
}

class MissingKeywordParametersDefaultFreeValuesInserterIntentionAction :
    MissingKeywordParametersInserterIntentionAction(RobotBundle.message("intention.family.keyword.insert.all.missing.parameters.without.default.values.name")) {

    override fun computeNewParameter(parameter: DefinedParameter, elementGenerator: RobotElementGenerator): RobotParameter =
        elementGenerator.createNewParameter(parameter.lookup, GlobalConstants.SUPER_SPACE)
}

class MissingKeywordParametersWithDefaultValuesInserterIntentionAction :
    MissingKeywordParametersInserterIntentionAction(RobotBundle.message("intention.family.keyword.insert.all.missing.parameters.with.default.values.name")) {

    override fun computeNewParameter(parameter: DefinedParameter, elementGenerator: RobotElementGenerator): RobotParameter {
        val parameterValue = parameter.defaultValue ?: GlobalConstants.SUPER_SPACE
        val newParameter = elementGenerator.createNewParameter(parameter.lookup, parameterValue)

        val reservedVariableFound = ReservedVariable.entries.any { it.variable.contentEquals(parameterValue, true) }
        if (reservedVariableFound) {
            elementGenerator.createNewScalarVariable(parameterValue)?.let { newParameter.positionalArgument?.firstChild?.replace(it) }
        }

        return newParameter
    }
}
