package dev.xeonkryptos.xeonrobotframeworkplugin.injection

import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.psi.PsiElement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpressionBody
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import org.intellij.plugins.intelliLang.inject.config.BaseInjection

class RobotLanguageInjectionContributor : LanguageInjectionContributor {

    override fun getInjection(context: PsiElement): Injection? {
        val visitor = PythonExpressionInjectionVisitor()
        context.accept(visitor)
        return if (visitor.pythonExpression) BaseInjection("python", "Python", visitor.prefix, visitor.suffix) else null
    }

    private class PythonExpressionInjectionVisitor : RobotVisitor() {

        var pythonExpression: Boolean = false
            private set

        var prefix: String = ""
            private set
        var suffix: String = ""
            private set

        override fun visitConditionalContent(o: RobotConditionalContent) {
            prefix = "if "
            suffix = ":"
        }

        override fun visitPythonExpression(o: RobotPythonExpression) {
            prefix = "result = "
        }

        override fun visitPythonExpressionBody(o: RobotPythonExpressionBody) {
            o.parent.accept(this)
            pythonExpression = true
        }
    }
}
