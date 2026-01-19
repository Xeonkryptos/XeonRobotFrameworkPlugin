package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor

class DuplicatedParameterInKeywordCallInspection() : LocalInspectionTool(), DumbAware {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor = KeywordCallVisitor(holder)

    private class KeywordCallVisitor(private val holder: ProblemsHolder) : RobotVisitor() {

        override fun visitKeywordCall(o: RobotKeywordCall) {
            o.parameterList.groupBy { it.parameterName }.filter { it.value.size > 1 }.forEach { (_, parameters) ->
                parameters.forEach { parameter ->
                    holder.registerProblem(parameter, RobotBundle.message("INSP.keyword.duplicated.parameters.defined.description"))
                }
            }
        }
    }
}
