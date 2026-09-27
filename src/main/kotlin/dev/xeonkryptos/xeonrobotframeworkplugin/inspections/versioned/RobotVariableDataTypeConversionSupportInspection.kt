package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.versioned

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotVersionBasedInspection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDatatypeDefinition
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider

class RobotVariableDataTypeConversionSupportInspection : RobotVersionBasedInspection() {

    override val minimumRobotVersion: RobotVersionProvider.RobotVersion = RobotVersionProvider.RobotVersion(7, 3, 0)

    override val negativeRobotVersionCheck: Boolean = true

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RobotVisitor() {
            override fun visitVariableDatatypeDefinition(o: RobotVariableDatatypeDefinition) {
                holder.registerProblem(o, RobotBundle.message("INSP.inspections.variable.data.type.conversion.not.supported"), ProblemHighlightType.WARNING)
            }
        }
    }
}
