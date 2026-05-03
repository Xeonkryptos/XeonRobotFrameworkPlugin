package dev.xeonkryptos.xeonrobotframeworkplugin.injection

import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.stubs.PyModuleNameIndex
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpressionBody
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames
import org.intellij.plugins.intelliLang.inject.config.BaseInjection

class RobotLanguageInjectionContributor : LanguageInjectionContributor {

    override fun getInjection(context: PsiElement): Injection? {
        val visitor = PythonExpressionInjectionVisitor(context.project)
        context.accept(visitor)
        return if (visitor.pythonExpression) BaseInjection("python", "Python", visitor.prefix, visitor.suffix) else null
    }

    private class PythonExpressionInjectionVisitor(private val project: Project) : RobotVisitor() {

        companion object {
            private val pythonModuleDetectionRegex = Regex("(\\p{javaJavaIdentifierStart}+\\.\\p{javaJavaIdentifierStart}*)+")
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

            val keywordCall = PsiTreeUtil.getParentOfType(o, RobotKeywordCall::class.java, true, RobotScalarVariable::class.java, RobotListVariable::class.java, RobotDictVariable::class.java)
            val additionalModuleImports = keywordCall?.let {
                val modules = mutableListOf<String>()
                val visitor = object : RobotVisitor() {
                    private var parameterUsed = false
                    private var argumentIndex = 0

                    override fun visitParameter(o: RobotParameter) {
                        if (o.parameterName == RobotNames.PARAMETER_MODULES) {
                            o.positionalArgument?.let { argument -> extractModules(argument) }
                        }
                        parameterUsed = true
                    }

                    override fun visitPositionalArgument(o: RobotPositionalArgument) {
                        if (!parameterUsed) {
                            argumentIndex++
                            if (argumentIndex == 1) extractModules(o)
                        }
                    }

                    private fun extractModules(argument: RobotPositionalArgument) = argument.text.split(Regex("\\s?,\\s?")).let { module -> modules.addAll(module) }
                }
                it.accept(visitor)
                modules
            } ?: emptyList()

            val moduleImports = mutableSetOf<String>()
            moduleImports.addAll(additionalModuleImports)
            moduleImports.addAll(computeImportsForPythonExpressionBodies(o.pythonExpressionBodyList))
            prefix = "${moduleImports.joinToString(separator = "\n", postfix = "\n") { module -> "import $module" }}\nresult = "
        }

        private fun computeImportsForPythonExpressionBodies(pythonExpressionBodies: Collection<RobotPythonExpressionBody>): Collection<String> {
            return pythonExpressionBodies.flatMap { pythonModuleDetectionRegex.findAll(it.text) }.map { it.value.split('.')[0] }.filter { PyModuleNameIndex.find(it, project, true).isNotEmpty() }
        }
    }
}
