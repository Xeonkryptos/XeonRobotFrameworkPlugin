package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.quickfix.RemoveElementQuickFix

class EmptyLocalArgumentsSettingInspection : LocalInspectionTool() {

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return object : RobotVisitor() {
            override fun visitLocalArgumentsSetting(o: RobotLocalArgumentsSetting) {
                if (o.localArgumentsSettingParameterList.isEmpty()) {
                    holder.registerProblem(
                        o,
                        RobotBundle.message("INSP.empty.local.arguments.setting.description"),
                        ProblemHighlightType.WARNING,
                        RemoveElementQuickFix(o)
                    )
                }
            }
        }
    }
}
