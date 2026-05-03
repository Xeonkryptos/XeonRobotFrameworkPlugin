package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.QualifiedName
import com.intellij.psi.util.parentOfType
import com.jetbrains.python.psi.stubs.PyModuleNameIndex
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class RobotEvaluateKeywordUnknownModulesInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object : RobotVisitor() {
            override fun visitPositionalArgument(o: RobotPositionalArgument) {
                val keywordCall = o.parentOfType<RobotKeywordCall>() ?: return
                if (!keywordCall.name.equals(RobotNames.EVALUATE_NORMALIZED_KEYWORD_NAME,
                        ignoreCase = true) && !keywordCall.name.equals("${RobotNames.BUILTIN_NAMESPACE}.${RobotNames.EVALUATE_NORMALIZED_KEYWORD_NAME}", ignoreCase = true)) return

                val parameter = o.parentOfType<RobotParameter>()
                val project = holder.project

                val virtualFile = o.containingFile?.originalFile?.virtualFile ?: return
                val module = ModuleManager.getInstance(project).modules.asSequence().firstOrNull { module -> ModuleRootManager.getInstance(module).fileIndex.isInContent(virtualFile) } ?: return
                if (parameter != null) {
                    if (!parameter.parameterName.equals(RobotNames.EVALUATE_MODULES_NORMALIZED_PARAMETER_NAME, ignoreCase = true)) return
                    identifyUnknownModules(o, module, holder)
                } else {
                    val potentialModulesArgument = keywordCall.allCallArguments.asSequence().drop(1).firstOrNull()
                    if (potentialModulesArgument != o) return

                    identifyUnknownModules(o, module, holder)
                }
            }
        }
    }

    private fun identifyUnknownModules(argument: RobotPositionalArgument, module: Module, holder: ProblemsHolder) {
        val modulesText = argument.text
        val moduleWithLibrariesScope = GlobalSearchScope.moduleWithLibrariesScope(module)
        modulesText.split(Regex("\\s?,\\s?"))
            .filter { PyModuleNameIndex.findByQualifiedName(QualifiedName.fromDottedString(it), argument.project, moduleWithLibrariesScope).isEmpty() }
            .forEach { unknownModule ->
                val indexOfUnknownModule = modulesText.indexOf(unknownModule)
                holder.registerProblem(argument,
                    RobotBundle.message("INSP.keyword.evaluate.parameters.module.unknown.module.description", unknownModule),
                    ProblemHighlightType.ERROR,
                    TextRange(indexOfUnknownModule, indexOfUnknownModule + unknownModule.length))
            }
    }
}
