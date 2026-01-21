package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineElseIfStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineElseStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceRunKeywordIfQuickFix(keywordCall: RobotKeywordCall) : AbstractReplaceKeywordQuickFix(keywordCall, RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME) {

    override fun isAvailable(keywordCall: RobotKeywordCall): Boolean {
        val conditionalStructureCollector = ConditionalStructureCollector()
        keywordCall.positionalArgumentList.forEach { positionalArgument -> positionalArgument.accept(conditionalStructureCollector) }
        return conditionalStructureCollector.conditionalContent != null && conditionalStructureCollector.ifKeywordCall != null
    }

    override fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall) {
        val conditionalStructureCollector = ConditionalStructureCollector()
        keywordCall.positionalArgumentList.forEach { it.accept(conditionalStructureCollector) }

        val ifCondition = conditionalStructureCollector.conditionalContent?.text ?: return
        val ifKeywordCall = conditionalStructureCollector.ifKeywordCall?.text ?: return

        val elseIfPairs = conditionalStructureCollector.inlineElseIfStructures.map { inlineElseIf ->
            Pair(inlineElseIf.conditionalContent.text, inlineElseIf.keywordCall?.text)
        }.toTypedArray()

        val elseConditionalBody = conditionalStructureCollector.inlineElseStructure?.keywordCall?.text

        val newConditionalStructure = RobotElementGenerator.getInstance(project).createNewConditionalStructure(ifCondition, ifKeywordCall, elseIfPairs, elseConditionalBody)
        keywordCall.replace(newConditionalStructure)
    }

    private class ConditionalStructureCollector : RobotVisitor() {

        var conditionalContent: RobotConditionalContent? = null
        var ifKeywordCall: RobotKeywordCall? = null
        val inlineElseIfStructures = mutableListOf<RobotInlineElseIfStructure>()
        var inlineElseStructure: RobotInlineElseStructure? = null

        override fun visitPositionalArgument(o: RobotPositionalArgument) {
            o.acceptChildren(this)
        }

        override fun visitConditionalContent(o: RobotConditionalContent) {
            conditionalContent = o
        }

        override fun visitKeywordCall(o: RobotKeywordCall) {
            ifKeywordCall = o
        }

        override fun visitInlineElseIfStructure(o: RobotInlineElseIfStructure) {
            inlineElseIfStructures.add(o)
        }

        override fun visitInlineElseStructure(o: RobotInlineElseStructure) {
            inlineElseStructure = o
        }
    }
}
