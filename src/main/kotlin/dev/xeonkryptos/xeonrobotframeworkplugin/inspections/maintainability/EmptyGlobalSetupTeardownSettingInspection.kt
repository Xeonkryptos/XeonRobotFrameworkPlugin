package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.quickfix.RemoveElementQuickFix

class EmptyGlobalSetupTeardownSettingInspection : LocalInspectionTool() {

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return object : RobotVisitor() {
            override fun visitSetupTeardownStatementsGlobalSetting(o: RobotSetupTeardownStatementsGlobalSetting) {
                if (o.keywordCall == null) {
                    holder.registerProblem(
                        o,
                        RobotBundle.message("INSP.empty.global.setup.teardown.setting.description"),
                        ProblemHighlightType.WARNING,
                        RemoveElementQuickFix(o)
                    )
                }
            }
        }
    }
}
