package dev.xeonkryptos.xeonrobotframeworkplugin.ide.patterns

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpressionBody

object RobotPatterns : PlatformPatterns() {

    @JvmStatic
    fun <T : PsiElement?> atFirstPositionOf(pattern: ElementPattern<out T?>): PatternCondition<T?> {
        return object : PatternCondition<T?>("atFirstPositionOf") {

            override fun accepts(t: T & Any, context: ProcessingContext?): Boolean {
                var previousElement: PsiElement? = null
                var element: PsiElement? = t
                while (element != null) {
                    if (pattern.accepts(element, context)) {
                        return true
                    }
                    if (previousElement != null) {
                        if (element.firstChild !== previousElement) {
                            return false
                        }
                    }
                    previousElement = element
                    element = element.context
                }
                return false
            }
        }
    }

    @JvmStatic
    @Suppress("unused")
    fun psiInlinePythonExpression(): ElementPattern<RobotPythonExpressionBody> = psiElement(RobotPythonExpressionBody::class.java)
}
