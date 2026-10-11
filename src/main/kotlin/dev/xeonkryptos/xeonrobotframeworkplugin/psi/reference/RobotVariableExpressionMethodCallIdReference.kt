package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCallId

/** Reference of the method name in a method call of an extended variable expression. It resolves to the method of the object the method is called on. */
class RobotVariableExpressionMethodCallIdReference(element: RobotVariableExpressionMethodCallId) :
    PsiPolyVariantReferenceBase<RobotVariableExpressionMethodCallId>(element, false) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val resolveCache = ResolveCache.getInstance(element.project)
        return resolveCache.resolveWithCaching(this, { reference, _ ->
            val id = reference.element
            RobotExtendedVariableResolver.resolveMember(id, id.text).map { PsiElementResolveResult(it) }.toTypedArray<ResolveResult>()
        }, false, false)
    }
}
