package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceReturnFromKeywordIfQuickFix(keywordCall: RobotKeywordCall) : AbstractReplaceKeywordQuickFix(keywordCall, RobotNames.RETURN_FROM_KEYWORD_NORMALIZED_KEYWORD_NAME) {

    override fun isAvailable(keywordCall: RobotKeywordCall): Boolean {
        val argumentsCollector = QuickFixArgumentsCollector()
        keywordCall.positionalArgumentList.forEach { it.accept(argumentsCollector) }
        return argumentsCollector.conditionalContent != null
    }

    override fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall) {
        val argumentsCollector = QuickFixArgumentsCollector()
        keywordCall.positionalArgumentList.forEach { it.accept(argumentsCollector) }

        val conditionalContent = argumentsCollector.conditionalContent?.text ?: return
        val returnValues = argumentsCollector.returnableContents.joinToString(GlobalConstants.SUPER_SPACE) { it.text }
        val returnStatement = "${RobotNames.RETURN_RESERVED_NAME}  $returnValues"

        val newConditionalStructure = RobotElementGenerator.getInstance(project).createNewConditionalStructure(conditionalContent, returnStatement, emptyArray(), null)
        keywordCall.replace(newConditionalStructure)
    }

    private class QuickFixArgumentsCollector : RobotVisitor() {

        var conditionalContent: RobotConditionalContent? = null
        val returnableContents = mutableListOf<RobotPositionalArgument>()

        override fun visitPositionalArgument(o: RobotPositionalArgument) {
            if (conditionalContent == null) o.acceptChildren(this)
            else returnableContents.add(o)
        }

        override fun visitConditionalContent(o: RobotConditionalContent) {
            conditionalContent = o
        }
    }
}
