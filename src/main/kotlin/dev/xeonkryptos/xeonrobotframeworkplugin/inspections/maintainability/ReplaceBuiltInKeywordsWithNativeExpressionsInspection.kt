package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotVersionBasedInspection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider

class ReplaceBuiltInKeywordsWithNativeExpressionsInspection : RobotVersionBasedInspection() {

    private val replaceableKeywordInfos = mapOf(*createReplaceableRunKeywordIfInfo(), *createReplaceableRunKeywordAndExpectError(), *createReplaceableReturnFromKeywordInfo())

    private fun createReplaceableRunKeywordIfInfo(): Array<Pair<String, (RobotKeywordCall) -> LocalQuickFix>> {
        val replaceableKeywordInfo = ::ReplaceRunKeywordIfQuickFix
        return arrayOf(
            Pair(RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME, replaceableKeywordInfo),
            Pair("${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME}", replaceableKeywordInfo)
        )
    }

    private fun createReplaceableRunKeywordAndExpectError(): Array<Pair<String, (RobotKeywordCall) -> LocalQuickFix>> {
        val replaceableKeywordInfo = ::ReplaceRunKeywordAndExpectErrorQuickFix
        return arrayOf(
            Pair(RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME, replaceableKeywordInfo),
            Pair("${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME}", replaceableKeywordInfo)
        )
    }

    private fun createReplaceableReturnFromKeywordInfo(): Array<Pair<String, (RobotKeywordCall) -> LocalQuickFix>> {
        val replaceableKeywordInfo = ::ReplaceReturnFromKeywordQuickFix
        return arrayOf(
            Pair(RobotNames.RETURN_FROM_KEYWORD_NORMALIZED_KEYWORD_NAME, replaceableKeywordInfo),
            Pair("${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RETURN_FROM_KEYWORD_NORMALIZED_KEYWORD_NAME}", replaceableKeywordInfo)
        )
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor = ReplaceableKeywordsVisitor(holder)

    override fun getMinimumRobotVersion(): RobotVersionProvider.RobotVersion = RobotVersionProvider.RobotVersion(5, 0, 0)

    private inner class ReplaceableKeywordsVisitor(private val holder: ProblemsHolder) : RobotVisitor() {

        override fun visitKeywordCall(keywordCall: RobotKeywordCall) {
            val normalizedKeywordCallName = KeywordUtil.normalizeKeywordName(keywordCall.name)
            replaceableKeywordInfos[normalizedKeywordCallName]?.let { quickFixCreator ->
                holder.registerProblem(keywordCall.keywordCallName, RobotBundle.message("INSP.keyword.replace.with.native.expressions.description"), quickFixCreator(keywordCall))
            }
        }
    }
}
