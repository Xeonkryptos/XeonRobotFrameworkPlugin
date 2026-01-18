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

    private val replaceableKeywordInfos = mapOf(*createReplaceableKeywordInfosForRobot4(), *createReplaceableKeywordInfosForRobot5())

    private fun createReplaceableKeywordInfosForRobot4(): Array<Pair<String, ReplaceableBuiltInKeywordsInfo>> {
        val replaceableKeywordInfo = ::ReplaceRunKeywordIfQuickFix
        val minimalRobotVersion = RobotVersionProvider.RobotVersion(4, 0, 0)
        return arrayOf(
            RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_IF_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableKeywordInfo)
        )
    }

    private fun createReplaceableKeywordInfosForRobot5(): Array<Pair<String, ReplaceableBuiltInKeywordsInfo>> {
        val replaceableRunKeywordAndExpectErrorKeywordInfo = ::ReplaceRunKeywordAndExpectErrorQuickFix
        val replaceableReturnFromKeywordKeywordInfo = ::ReplaceReturnFromKeywordQuickFix
        val replaceableReturnFromKeywordIfKeywordInfo = ::ReplaceReturnFromKeywordIfQuickFix
        val replaceableRunKeywordAndReturnKeywordInfo = ::ReplaceRunKeywordAndReturnQuickFix
        val replaceableRunKeywordAndReturnIfKeywordInfo = ::ReplaceRunKeywordAndReturnIfQuickFix
        val replaceableContinueForLoopKeywordInfo = ::ReplaceContinueForLoopQuickFix
        val replaceableContinueForLoopIfKeywordInfo = ::ReplaceContinueForLoopIfQuickFix
        val replaceableExitForLoopKeywordInfo = ::ReplaceExitForLoopQuickFix
        val replaceableExitForLoopIfKeywordInfo = ::ReplaceExitForLoopIfQuickFix

        val minimalRobotVersion = RobotVersionProvider.RobotVersion(5, 0, 0)
        return arrayOf(
            RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableRunKeywordAndExpectErrorKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(
                minimalRobotVersion, replaceableRunKeywordAndExpectErrorKeywordInfo
            ),

            RobotNames.RETURN_FROM_KEYWORD_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableReturnFromKeywordKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RETURN_FROM_KEYWORD_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableReturnFromKeywordKeywordInfo),

            RobotNames.RETURN_FROM_KEYWORD_IF_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableReturnFromKeywordIfKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RETURN_FROM_KEYWORD_IF_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(
                minimalRobotVersion, replaceableReturnFromKeywordIfKeywordInfo
            ),

            RobotNames.RUN_KEYWORD_AND_RETURN_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableRunKeywordAndReturnKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_AND_RETURN_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(
                minimalRobotVersion, replaceableRunKeywordAndReturnKeywordInfo
            ),

            RobotNames.RUN_KEYWORD_AND_RETURN_IF_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableRunKeywordAndReturnIfKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.RUN_KEYWORD_AND_RETURN_IF_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(
                minimalRobotVersion, replaceableRunKeywordAndReturnIfKeywordInfo
            ),

            RobotNames.CONTINUE_FOR_LOOP_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableContinueForLoopKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.CONTINUE_FOR_LOOP_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(
                minimalRobotVersion, replaceableContinueForLoopKeywordInfo
            ),

            RobotNames.CONTINUE_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableContinueForLoopIfKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.CONTINUE_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(
                minimalRobotVersion, replaceableContinueForLoopIfKeywordInfo
            ),

            RobotNames.EXIT_FOR_LOOP_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableExitForLoopKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.EXIT_FOR_LOOP_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(
                minimalRobotVersion, replaceableExitForLoopKeywordInfo
            ),

            RobotNames.EXIT_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME to ReplaceableBuiltInKeywordsInfo(minimalRobotVersion, replaceableExitForLoopIfKeywordInfo),
            "${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.EXIT_FOR_LOOP_IF_NORMALIZED_KEYWORD_NAME}" to ReplaceableBuiltInKeywordsInfo(
                minimalRobotVersion, replaceableExitForLoopIfKeywordInfo
            )
        )
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor = ReplaceableKeywordsVisitor(holder, session)

    override fun getMinimumRobotVersion(): RobotVersionProvider.RobotVersion = RobotVersionProvider.RobotVersion(4, 0, 0)

    private inner class ReplaceableKeywordsVisitor(private val holder: ProblemsHolder, private val session: LocalInspectionToolSession) : RobotVisitor() {

        override fun visitKeywordCall(keywordCall: RobotKeywordCall) {
            val normalizedKeywordCallName = KeywordUtil.normalizeKeywordName(keywordCall.name)
            replaceableKeywordInfos[normalizedKeywordCallName]?.let { info ->
                val currentRobotVersion = getRobotVersion(session)
                if (currentRobotVersion.supports(info.minimalRobotVersion)) {
                    holder.registerProblem(keywordCall.keywordCallName, RobotBundle.message("INSP.keyword.replace.with.native.expressions.description"), info.quickFixCreator(keywordCall))
                }
            }
        }
    }

    private data class ReplaceableBuiltInKeywordsInfo(val minimalRobotVersion: RobotVersionProvider.RobotVersion, val quickFixCreator: (RobotKeywordCall) -> LocalQuickFix)
}
