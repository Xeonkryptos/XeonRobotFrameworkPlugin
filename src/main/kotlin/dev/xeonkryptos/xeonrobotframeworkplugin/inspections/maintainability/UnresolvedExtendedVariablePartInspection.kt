package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiPolyVariantReference
import com.jetbrains.python.documentation.PythonDocumentationProvider
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionDefinition
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCallId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotExtendedVariableResolver
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotFileManager

/**
 * Warns about parts of the extended variable syntax (`${OBJECT.attribute.method()}`) which can't be resolved. Only the first unresolvable part of an
 * expression is reported as all parts behind it depend on it and can't be resolved for that reason. Parts behind a nested variable are skipped as nothing can
 * be said about what that variable evaluates to.
 */
class UnresolvedExtendedVariablePartInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : RobotVisitor() {
            override fun visitVariableExpressionDefinition(o: RobotVariableExpressionDefinition) {
                val segments = RobotExtendedVariableResolver.segmentsOf(o)
                for ((index, segment) in segments.withIndex()) {
                    segment ?: return
                    if (!isResolvable(segment, isBase = index == 0)) {
                        holder.registerProblem(segment, createMessage(segment, isBase = index == 0), ProblemHighlightType.WARNING)
                        break
                    }
                }
            }
        }
    }

    private fun isResolvable(segment: PsiElement, isBase: Boolean): Boolean {
        val reference = when (segment) {
            is RobotVariableExpressionId -> segment.reference
            is RobotVariableExpressionMethodCallId -> segment.reference
            else -> return true
        } as? PsiPolyVariantReference ?: return true
        if (reference.multiResolve(false).isNotEmpty()) return true

        // Built-in variables like ${TEST_NAME} don't resolve to a definition, but do exist
        return isBase && RobotFileManager.getGlobalVariables(segment).orEmpty().any { it.matches(segment.text) }
    }

    private fun createMessage(segment: PsiElement, isBase: Boolean): String {
        val name = segment.text
        if (isBase) return RobotBundle.message("INSP.unresolved.extended.variable.part.variable", name)

        val context = RobotExtendedVariableResolver.typeEvalContext(segment)
        val receiverTypes = RobotExtendedVariableResolver.receiverTypes(segment, context)
        if (receiverTypes.isEmpty()) return RobotBundle.message("INSP.unresolved.extended.variable.part.unknown.type", name)

        val typeNames = receiverTypes.joinToString(" | ") { PythonDocumentationProvider.getTypeName(it, context) }
        return RobotBundle.message("INSP.unresolved.extended.variable.part.member", name, typeNames)
    }
}
