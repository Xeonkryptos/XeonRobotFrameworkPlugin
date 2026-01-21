package dev.xeonkryptos.xeonrobotframeworkplugin.rename

import com.intellij.psi.PsiElement
import com.intellij.refactoring.listeners.RefactoringElementListener
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import com.intellij.refactoring.rename.RenameUtil
import com.intellij.usageView.UsageInfo
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil

class RobotVariableDefinitionRenameElementProcessor : RenamePsiElementProcessor() {

    override fun canProcessElement(element: PsiElement): Boolean = element is RobotVariableDefinition

    override fun renameElement(
        element: PsiElement, newName: String, usages: Array<out UsageInfo>, listener: RefactoringElementListener?
    ) {
        val newBaseName = VariableNameUtil.computeBaseVariableName(newName)
        val namesForUsages = mutableMapOf<String, MutableList<UsageInfo>>()
        usages.forEach { usage ->
            val variableName = when (usage.element) {
                is RobotVariableBodyId -> (usage.element as RobotVariableBodyId).text
                is RobotVariable -> (usage.element as RobotVariable).variableName
                is RobotVariableDefinition -> (usage.element as RobotVariableDefinition).name
                else -> null
            }
            if (variableName == null || variableName == newBaseName) {
                namesForUsages.computeIfAbsent(newName) { mutableListOf() }.add(usage)
            } else {
                val baseNameTextRange = VariableNameUtil.computeBaseVariableNameTextRange(variableName)
                val newNameForUsage = baseNameTextRange.replace(variableName, newBaseName)
                namesForUsages.computeIfAbsent(newNameForUsage) { mutableListOf() }.add(usage)
            }
        }
        namesForUsages.forEach { (name, usagesForName) ->
            RenameUtil.doRenameGenericNamedElement(element, name, usagesForName.toTypedArray(), listener)
        }
    }
}
