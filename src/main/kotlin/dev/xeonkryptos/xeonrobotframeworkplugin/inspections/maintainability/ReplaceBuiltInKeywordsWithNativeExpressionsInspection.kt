package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.codeInspection.util.InspectionMessage
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceBuiltInKeywordsWithNativeExpressionsInspection : LocalInspectionTool() {

    private val replaceableKeywordInfos = mapOf<String, ReplaceableKeywordInfo>(*createReplaceableRunKeywordIfInfo(), *createReplaceableRunKeywordAndExpectError())

    private fun createReplaceableRunKeywordIfInfo(): Array<Pair<String, ReplaceableKeywordInfo>> {
        val replaceableKeywordInfo = ReplaceableKeywordInfo("", ::ReplaceRunKeywordIfQuickFix)
        return arrayOf(
            Pair(RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME, replaceableKeywordInfo),
            Pair("${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME}", replaceableKeywordInfo)
        )
    }

    private fun createReplaceableRunKeywordAndExpectError(): Array<Pair<String, ReplaceableKeywordInfo>> {
        val replaceableKeywordInfo = ReplaceableKeywordInfo("", ::ReplaceRunKeywordAndExpectErrorQuickFix)
        return arrayOf(
            Pair(RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME, replaceableKeywordInfo),
            Pair("${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME}", replaceableKeywordInfo)
        )
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return ReplaceableKeywordsVisitor(holder)
    }

    private inner class ReplaceableKeywordsVisitor(private val holder: ProblemsHolder) : RobotVisitor() {

        override fun visitKeywordCall(keywordCall: RobotKeywordCall) {
            val normalizedKeywordCallName = KeywordUtil.normalizeKeywordName(keywordCall.name)
            replaceableKeywordInfos[normalizedKeywordCallName]?.let { info ->
                holder.registerProblem(keywordCall.keywordCallName, info.messageKey, info.quickFixCreator(keywordCall))
            }
        }
    }

    private data class ReplaceableKeywordInfo(@InspectionMessage val messageKey: String, val quickFixCreator: (RobotKeywordCall) -> LocalQuickFix)
}
