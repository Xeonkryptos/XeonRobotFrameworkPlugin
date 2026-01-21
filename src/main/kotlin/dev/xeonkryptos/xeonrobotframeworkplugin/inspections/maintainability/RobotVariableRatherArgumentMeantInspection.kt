package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLiteralConstantValue
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable

class RobotVariableRatherArgumentMeantInspection : LocalInspectionTool(), DumbAware {

    override fun buildVisitor(
        holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return object : RobotVisitor() {
            override fun visitLiteralConstantValue(o: RobotLiteralConstantValue) {
                if (o.parent is RobotPositionalArgument) {
                    val argumentText = o.text
                    val reservedVariableFound = ReservedVariable.entries.any { it.unwrappedVariable.contentEquals(argumentText, true) }
                    if (reservedVariableFound) {
                        holder.registerProblem(
                            o,
                            RobotBundle.message("INSP.variable.rather.argument.meant.description"),
                            ProblemHighlightType.WARNING,
                            RobotVariableRatherArgumentQuickFix(o)
                        )
                    }
                }
            }
        }
    }
}
