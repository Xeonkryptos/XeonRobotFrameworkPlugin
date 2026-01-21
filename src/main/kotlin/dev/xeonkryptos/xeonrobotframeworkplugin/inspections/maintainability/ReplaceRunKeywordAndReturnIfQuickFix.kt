package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceRunKeywordAndReturnIfQuickFix(keywordCall: RobotKeywordCall) : AbstractReplaceKeywordQuickFix(keywordCall, RobotNames.RUN_KEYWORD_AND_RETURN_IF_NORMALIZED_KEYWORD_NAME) {

    override fun isAvailable(keywordCall: RobotKeywordCall): Boolean = keywordCall.positionalArgumentList.isNotEmpty()

    override fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall) {
        val argumentsCollector = QuickFixArgumentsCollector()
        keywordCall.positionalArgumentList.forEach { it.accept(argumentsCollector) }

        val conditionalContent = argumentsCollector.conditionalContent?.text ?: return
        val subKeywordCall = argumentsCollector.subKeywordCall?.text ?: return

        val conditionalBodyWithReturn = """
            ${'$'}{Result}=  $subKeywordCall
            ${RobotNames.RETURN_RESERVED_NAME}  ${'$'}{Result}
        """.trimIndent()
        val conditionalStructure = RobotElementGenerator.getInstance(project).createNewConditionalStructure(conditionalContent, conditionalBodyWithReturn, emptyArray(), null)

        keywordCall.replace(conditionalStructure)
    }

    private class QuickFixArgumentsCollector : RobotVisitor() {

        var conditionalContent: RobotConditionalContent? = null
        var subKeywordCall: RobotKeywordCall? = null

        override fun visitPositionalArgument(o: RobotPositionalArgument) {
            o.acceptChildren(this)
        }

        override fun visitConditionalContent(o: RobotConditionalContent) {
            conditionalContent = o
        }

        override fun visitKeywordCall(o: RobotKeywordCall) {
            subKeywordCall = o
        }
    }
}
