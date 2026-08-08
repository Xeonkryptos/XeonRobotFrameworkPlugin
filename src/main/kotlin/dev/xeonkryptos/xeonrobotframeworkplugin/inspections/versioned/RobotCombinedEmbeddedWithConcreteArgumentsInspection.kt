package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.versioned

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotVersionBasedInspection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider

class RobotCombinedEmbeddedWithConcreteArgumentsInspection : RobotVersionBasedInspection() {

    private val userKeywordResultsHolderKey = Key.create<Map<RobotUserKeywordStatement, Boolean>>("USER_KEYWORD_RESULT_HOLDER")

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        val userKeywordResultHolder = mutableMapOf<RobotUserKeywordStatement, Boolean>()
        session.putUserData(userKeywordResultsHolderKey, userKeywordResultHolder)
        return object : RobotVisitor() {
            override fun visitUserKeywordStatementId(o: RobotUserKeywordStatementId) {
                val usesEmbeddedArguments = o.childrenOfType<RobotVariableDefinition>().isNotEmpty()
                if (usesEmbeddedArguments) userKeywordResultHolder.merge(o.parent as RobotUserKeywordStatement, false) { _, _ -> true }
            }

            override fun visitLocalArgumentsSetting(o: RobotLocalArgumentsSetting) {
                o.parentOfType<RobotUserKeywordStatement>()?.let {
                    userKeywordResultHolder.merge(it, false) { _, _ -> true }
                }
            }
        }
    }

    override fun inspectionFinished(session: LocalInspectionToolSession, problemsHolder: ProblemsHolder) {
        session.getUserData(userKeywordResultsHolderKey)?.let {
            it.entries.asSequence().filter { entry -> entry.value }.map { entry -> entry.key }.forEach { userKeywordStatement ->
                problemsHolder.registerProblem(userKeywordStatement, RobotBundle.message("INSP.feature.combined.embedded.with.concrete.arguments.description"), ProblemHighlightType.ERROR)
            }
        }
    }

    override fun getMinimumRobotVersion(): RobotVersionProvider.RobotVersion = RobotVersionProvider.RobotVersion(6, 1, 0)
}
