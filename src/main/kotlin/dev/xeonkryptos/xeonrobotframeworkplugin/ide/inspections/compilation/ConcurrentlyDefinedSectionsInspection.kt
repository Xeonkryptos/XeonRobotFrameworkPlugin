package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor

class ConcurrentlyDefinedSectionsInspection : LocalInspectionTool(), DumbAware {

    private val concurrentlyDefinedSectionsVisitorKey = Key.create<ConcurrentlyDefinedSectionsVisitor>("CONCURRENTLY_DEFINED_SECTIONS_VISITOR")

    override fun runForWholeFile(): Boolean = true

    override fun buildVisitor(
        holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession
    ): PsiElementVisitor {
        val visitor = ConcurrentlyDefinedSectionsVisitor()
        session.putUserData(concurrentlyDefinedSectionsVisitorKey, visitor)
        return visitor
    }

    override fun inspectionFinished(
        session: LocalInspectionToolSession, problemsHolder: ProblemsHolder
    ) {
        val visitor = session.getUserData(concurrentlyDefinedSectionsVisitorKey) ?: return

        val testCasesSection = visitor.testCasesSection
        val tasksSection = visitor.tasksSection

        if (testCasesSection != null && tasksSection != null) {
            problemsHolder.registerProblem(
                testCasesSection.nameIdentifier, RobotBundle.message("INSP.concurrently.defined.sections.description"), ProblemHighlightType.ERROR
            )
            problemsHolder.registerProblem(
                tasksSection.nameIdentifier, RobotBundle.message("INSP.concurrently.defined.sections.description"), ProblemHighlightType.ERROR
            )
        }
    }

    private class ConcurrentlyDefinedSectionsVisitor : RobotVisitor() {

        var testCasesSection: RobotTestCasesSection? = null
        var tasksSection: RobotTasksSection? = null

        override fun visitTestCasesSection(o: RobotTestCasesSection) {
            testCasesSection = o
        }

        override fun visitTasksSection(o: RobotTasksSection) {
            tasksSection = o
        }
    }
}
