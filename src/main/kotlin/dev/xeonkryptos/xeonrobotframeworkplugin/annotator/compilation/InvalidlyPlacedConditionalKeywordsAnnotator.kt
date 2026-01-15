package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineElseIfStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineElseStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class InvalidlyPlacedConditionalKeywordsAnnotator : RobotAnnotator() {

    override fun visitKeywordCall(keywordCall: RobotKeywordCall) {
        val normalizedKeywordName = KeywordUtil.normalizeKeywordName(keywordCall.keywordCallName.keywordName.text)
        if (normalizedKeywordName == RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME) {
            val inlineElseStructureArguments = keywordCall.positionalArgumentList.filter { arg -> arg.childrenOfType<RobotInlineElseStructure>().isNotEmpty() }
            if (inlineElseStructureArguments.size > 1) {
                inlineElseStructureArguments.asSequence()
                    .drop(1)
                    .forEach { holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.inline-conditional.else.more-than-one")).range(it).create() }
            }
        }
    }

    override fun visitInlineElseIfStructure(o: RobotInlineElseIfStructure) = evaluatePositionOfConditionalInlineStatement(o)

    override fun visitInlineElseStructure(o: RobotInlineElseStructure) = evaluatePositionOfConditionalInlineStatement(o)

    private fun evaluatePositionOfConditionalInlineStatement(element: PsiElement) {
        element.parentOfType<RobotPositionalArgument>()?.let {
            val keywordCall = it.parentOfType<RobotKeywordCall>()
            if (keywordCall == null) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.inline-conditional.invalid")).range(element).create()
            } else {
                val normalizedKeywordName = KeywordUtil.normalizeKeywordName(keywordCall.keywordCallName.keywordName.text)
                if (normalizedKeywordName != RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME) {
                    holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.inline-conditional.invalid")).range(element).create()
                }
            }
        }
    }
}
