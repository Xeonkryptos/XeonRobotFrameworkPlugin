package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElementVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.NameIdentifierHolder
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class DuplicatedSectionsInspection : LocalInspectionTool(), DumbAware {

    private val duplicatedSectionsVisitorKey = Key.create<DuplicatedSectionsVisitor>("DUPLICATED_SECTIONS_VISITOR")

    override fun buildVisitor(
        holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession
    ): PsiElementVisitor {
        val visitor = DuplicatedSectionsVisitor()
        session.putUserData(duplicatedSectionsVisitorKey, visitor)
        return visitor
    }

    override fun inspectionFinished(
        session: LocalInspectionToolSession, problemsHolder: ProblemsHolder
    ) {
        val visitor = session.getUserData(duplicatedSectionsVisitorKey) ?: return

        visitor.typedSections.forEach { (_, duplicatedSections) ->
            if (duplicatedSections.size > 1) {
                duplicatedSections.forEach { section ->
                    problemsHolder.registerProblem(
                        section.nameIdentifier, RobotBundle.message("INSP.duplicated.section.description"), ProblemHighlightType.ERROR
                    )
                }
            }
        }
    }

    private class DuplicatedSectionsVisitor : RobotVisitor() {

        val typedSections = ConcurrentHashMap<Class<out RobotSection>, CopyOnWriteArrayList<NameIdentifierHolder>>()

        override fun visitSection(o: RobotSection) {
            typedSections.computeIfAbsent(o::class.java) { CopyOnWriteArrayList() }.add(o as NameIdentifierHolder)
        }
    }
}
