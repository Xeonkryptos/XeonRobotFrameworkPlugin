package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceExitForLoopIfQuickFix(keywordCall: RobotKeywordCall) : AbstractReplaceKeywordQuickFix(keywordCall, RobotNames.EXIT_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME) {

    override fun isAvailable(keywordCall: RobotKeywordCall): Boolean {
        val argumentsCollector = QuickFixArgumentsCollector()
        keywordCall.positionalArgumentList.forEach { it.accept(argumentsCollector) }
        return argumentsCollector.conditionalContent != null
    }

    override fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall) {
        val argumentsCollector = QuickFixArgumentsCollector()
        keywordCall.positionalArgumentList.forEach { it.accept(argumentsCollector) }

        val conditionalContent = argumentsCollector.conditionalContent?.text ?: return

        val loopControlStructure =
            RobotElementGenerator.getInstance(project).createNewConditionalStructure(conditionalContent, RobotElementGenerator.LoopControlStructureType.BREAK.name, emptyArray(), null)
        keywordCall.replace(loopControlStructure)
    }

    private class QuickFixArgumentsCollector : RobotVisitor() {

        var conditionalContent: RobotConditionalContent? = null

        override fun visitPositionalArgument(o: RobotPositionalArgument) {
            o.acceptChildren(this)
        }

        override fun visitConditionalContent(o: RobotConditionalContent) {
            conditionalContent = o
        }
    }
}
