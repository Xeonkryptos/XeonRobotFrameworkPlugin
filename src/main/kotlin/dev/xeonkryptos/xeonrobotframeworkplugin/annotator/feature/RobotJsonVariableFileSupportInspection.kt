package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.feature

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotVersionBasedInspection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider

class RobotJsonVariableFileSupportInspection : RobotVersionBasedInspection(), DumbAware {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : RobotVisitor() {
            override fun visitImportArgument(o: RobotImportArgument) {
                if (o.text.endsWith(".json")) {
                    holder.registerProblem(o, RobotBundle.message("INSP.feature.json.variable.file.import.description"), ProblemHighlightType.ERROR)
                }
            }
        }
    }

    override fun getMinimumRobotVersion(): RobotVersionProvider.RobotVersion = RobotVersionProvider.RobotVersion(6, 1, 0)
}
