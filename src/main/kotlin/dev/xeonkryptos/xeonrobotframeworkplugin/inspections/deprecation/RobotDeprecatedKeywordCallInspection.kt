package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.deprecation

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.DeprecationInspector

class RobotDeprecatedKeywordCallInspection : LocalInspectionTool() {

    override fun buildVisitor(
        holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return DeprecatedRobotKeywordCallPyFunctionVisitor(holder)
    }

    private class DeprecatedRobotKeywordCallPyFunctionVisitor(private val holder: ProblemsHolder) : RobotVisitor() {
        override fun visitKeywordCallName(o: RobotKeywordCallName) {
            o.reference.resolve()?.let { referencedElement ->
                if (DeprecationInspector.isDeprecated(referencedElement)) {
                    holder.registerProblem(o, RobotBundle.message("INSP.keyword-call.deprecated"), ProblemHighlightType.LIKE_DEPRECATED)
                }
            }
        }
    }
}
