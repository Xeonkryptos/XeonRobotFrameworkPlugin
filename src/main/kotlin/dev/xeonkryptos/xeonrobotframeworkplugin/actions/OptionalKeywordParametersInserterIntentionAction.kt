package dev.xeonkryptos.xeonrobotframeworkplugin.actions

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

class OptionalKeywordParametersInserterIntentionAction : PsiElementBaseIntentionAction() {

    init {
        text = familyName
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val keywordCall = element.parentOfType<RobotKeywordCall>(false) ?: return
        val missingParameters = keywordCall.computeMissingParameters()

        val elementGenerator = RobotElementGenerator.getInstance(project)
        val parserFacade = PsiParserFacade.getInstance(project)
        missingParameters.filter { !it.isPositionalOnly && !it.isPositionalContainer && !it.isKeywordContainer }.forEach { parameter ->
            val parameterValue = parameter.defaultValue ?: GlobalConstants.SUPER_SPACE
            val newParameter = elementGenerator.createNewParameter(parameter.lookup, parameterValue)
            val eolNode = keywordCall.lastChild ?: return

            val addedParameterElement = keywordCall.addBefore(newParameter, eolNode)
            val superSpaceElement = parserFacade.createWhiteSpaceFromText(GlobalConstants.SUPER_SPACE)
            keywordCall.addBefore(superSpaceElement, addedParameterElement)
        }
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.node.elementType === RobotTypes.KEYWORD_NAME || element.node.elementType === RobotTypes.KEYWORD_LIBRARY_NAME) {
            val keywordCall = element.parentOfType<RobotKeywordCall>(false) ?: return false
            if (keywordCall.lastChild?.node?.elementType !== RobotTypes.EOL) return false

            return keywordCall.computeMissingParameters().any { !it.isPositionalOnly && !it.isPositionalContainer && !it.isKeywordContainer }
        }
        return false
    }

    override fun getFamilyName(): @IntentionFamilyName String = RobotBundle.message("intention.family.keyword.insert.all.missing.parameters.name")
}
