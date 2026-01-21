package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceRunKeywordAndReturnQuickFix(keywordCall: RobotKeywordCall) : AbstractReplaceKeywordQuickFix(keywordCall, RobotNames.RUN_KEYWORD_AND_RETURN_NORMALIZED_KEYWORD_NAME) {

    override fun isAvailable(keywordCall: RobotKeywordCall): Boolean = keywordCall.positionalArgumentList.isNotEmpty()

    override fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall) {
        val subKeywordCall = keywordCall.positionalArgumentList.firstOrNull()?.text ?: return

        val robotElementGenerator = RobotElementGenerator.getInstance(project)
        val keywordVariableDefinition = robotElementGenerator.createNewKeywordVariableDefinition("Result", subKeywordCall)
        val returnStructure = robotElementGenerator.createNewReturnStructure("\${Result}")

        val parent = keywordCall.parent
        keywordCall.replace(keywordVariableDefinition)
        parent.addAfter(returnStructure, keywordVariableDefinition)
    }
}
