package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceRunKeywordAndExpectErrorQuickFix(keywordCall: RobotKeywordCall) : AbstractReplaceKeywordQuickFix(keywordCall, RobotNames.RUN_KEYWORD_AND_EXPECT_ERROR_NORMALIZED_KEYWORD_NAME) {

    override fun isAvailable(keywordCall: RobotKeywordCall): Boolean = keywordCall.positionalArgumentList.size == 2

    override fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall) {
        val quickFixArgumentsCollector = QuickFixArgumentsCollector()
        keywordCall.positionalArgumentList.forEach { it.accept(quickFixArgumentsCollector) }

        val expectedError = quickFixArgumentsCollector.expectedError?.text ?: return
        val subKeywordCall = quickFixArgumentsCollector.keywordCall?.text ?: return

        val newConditionalStructure =
            RobotElementGenerator.getInstance(project).createNewExceptionHandlingStructure(subKeywordCall, arrayOf(Pair(expectedError, "Fail  Expected error not raised.")), null)
        keywordCall.replace(newConditionalStructure)
    }

    private class QuickFixArgumentsCollector : RobotVisitor() {

        var expectedError: RobotPositionalArgument? = null
        var keywordCall: RobotKeywordCall? = null

        override fun visitPositionalArgument(o: RobotPositionalArgument) {
            if (expectedError == null) expectedError = o
            else o.acceptChildren(this)
        }

        override fun visitKeywordCall(o: RobotKeywordCall) {
            keywordCall = o
        }
    }
}
