package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineElseIfStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineElseStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceRunKeywordIfQuickFix(keywordCall: RobotKeywordCall) : LocalQuickFixOnPsiElement(keywordCall) {

    companion object {
        private val replaceableKeywordNames = setOf(RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME, "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME}")
    }

    override fun getText(): @IntentionName String = RobotBundle.message("intention.family.deprecated.keyword.native.replacement.name")

    override fun isAvailable(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement): Boolean = startElement.parentOfType<RobotKeywordCall>(withSelf = true)?.let {
        val normalizeKeywordName = KeywordUtil.normalizeKeywordName(it.name)
        // The quick fix is not available for keyword calls with parameters. A keyword call like "Run Keyword If" needs positional arguments.
        return@let if (replaceableKeywordNames.contains(normalizeKeywordName) && it.parameterList.isEmpty()) {
            val conditionalStructureCollector = ConditionalStructureCollector()
            it.positionalArgumentList.forEach { positionalArgument -> positionalArgument.accept(conditionalStructureCollector) }
            conditionalStructureCollector.conditionalContent != null && conditionalStructureCollector.ifKeywordCall != null
        } else false
    } ?: false

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val runKeywordIfCall = startElement.parentOfType<RobotKeywordCall>(withSelf = true) ?: return

        val conditionalStructureCollector = ConditionalStructureCollector()
        runKeywordIfCall.positionalArgumentList.forEach { it.accept(conditionalStructureCollector) }

        val ifCondition = conditionalStructureCollector.conditionalContent?.text ?: return
        val ifKeywordCall = conditionalStructureCollector.ifKeywordCall?.text ?: return

        val elseIfPairs = conditionalStructureCollector.inlineElseIfStructures.map { inlineElseIf ->
            Pair(inlineElseIf.conditionalContent.text, inlineElseIf.keywordCall?.text)
        }.toTypedArray()

        val elseConditionalBody = conditionalStructureCollector.inlineElseStructure?.keywordCall?.text

        val newConditionalStructure = RobotElementGenerator.getInstance(project).createNewConditionalStructure(ifCondition, ifKeywordCall, elseIfPairs, elseConditionalBody)
        startElement.replace(newConditionalStructure)
    }

    override fun getFamilyName(): @IntentionFamilyName String = RobotBundle.message("intention.family.deprecated.keyword.native.replacement.text")

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
