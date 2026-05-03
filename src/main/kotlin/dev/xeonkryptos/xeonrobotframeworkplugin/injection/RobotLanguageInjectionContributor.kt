package dev.xeonkryptos.xeonrobotframeworkplugin.injection

import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.python.psi.stubs.PyModuleNameIndex
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpressionBody
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import org.intellij.plugins.intelliLang.inject.config.BaseInjection

class RobotLanguageInjectionContributor : LanguageInjectionContributor {

    override fun getInjection(context: PsiElement): Injection? {
        val visitor = PythonExpressionInjectionVisitor(context.project)
        context.accept(visitor)
        return if (visitor.pythonExpression) BaseInjection("python", "Python", visitor.prefix, visitor.suffix) else null
    }

    private class PythonExpressionInjectionVisitor(private val project: Project) : RobotVisitor() {

        companion object {
            private val pythonModuleDetectionRegex = Regex("(\\p{javaJavaIdentifierStart}+\\.\\p{javaJavaIdentifierStart}+)+")
        }

        var pythonExpression: Boolean = false
            private set

        var prefix: String = ""
            private set
        var suffix: String = ""
            private set

        override fun visitConditionalContent(o: RobotConditionalContent) {
            pythonExpression = true
            prefix = "${computeImportsForPythonExpressionBodies(o.pythonExpressionBodyList)}if "
            suffix = ":\n  pass"
        }

        override fun visitPythonExpression(o: RobotPythonExpression) {
            pythonExpression = true
            prefix = "${computeImportsForPythonExpressionBodies(o.pythonExpressionBodyList)}\nresult = "
        }

        private fun computeImportsForPythonExpressionBodies(pythonExpressionBodies: Collection<RobotPythonExpressionBody>): String {
            return pythonExpressionBodies.flatMap { pythonModuleDetectionRegex.findAll(it.text) }
                .map { it.value.split('.')[0] }
                .filter { PyModuleNameIndex.find(it, project, true).isNotEmpty() }
                .joinToString(separator = "\n", postfix = "\n") { module -> "import $module" }
        }
    }
}
