package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionId

/**
 * Reference of a part in an extended variable expression. The base variable resolves to the variable definitions, any following part to the attribute or
 * property of the object before.
 */
class RobotVariableExpressionIdReference(element: RobotVariableExpressionId) : PsiPolyVariantReferenceBase<RobotVariableExpressionId>(element, false) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val resolveCache = ResolveCache.getInstance(element.project)
        return resolveCache.resolveWithCaching(this, { reference, _ ->
            val id = reference.element
            val resolved = if (RobotExtendedVariableResolver.isBase(id)) RobotExtendedVariableResolver.resolveBase(id) else RobotExtendedVariableResolver.resolveMember(id, id.text)
            resolved.map { PsiElementResolveResult(it) }.toTypedArray<ResolveResult>()
        }, false, false)
    }
}
