package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.psi.PsiElementVisitor
import com.intellij.testIntegration.TestFailedLineManager
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor

class TestFailedLineInspection : LocalInspectionTool() {

    companion object {
        private const val TEST_FAILED_MAGNITUDE = 6 // see TestStateInfo#Magnitude
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor =
        TestFailedVisitor(holder, isOnTheFly)

    class TestFailedVisitor(private val holder: ProblemsHolder, private val isOnTheFly: Boolean) : RobotVisitor() {

        override fun visitKeywordCall(keywordCall: RobotKeywordCall) {
            val testFailProvider = TestFailedLineManager.getInstance(holder.project)
            val testInfo = testFailProvider.getTestInfo(keywordCall) ?: return
            if (testInfo.magnitude < TEST_FAILED_MAGNITUDE) return // don't highlight skipped tests

            val fixes = listOfNotNull(
                testFailProvider.getDebugQuickFix(keywordCall, testInfo.topStackTraceLine), testFailProvider.getRunQuickFix(keywordCall)
            ).toTypedArray()
            val descriptor = InspectionManager.getInstance(holder.project).createProblemDescriptor(
                keywordCall.keywordCallName, testInfo.errorMessage, isOnTheFly, fixes, ProblemHighlightType.GENERIC_ERROR_OR_WARNING
            ).apply { setTextAttributes(CodeInsightColors.RUNTIME_ERROR) }
            holder.registerProblem(descriptor)
        }
    }
}
