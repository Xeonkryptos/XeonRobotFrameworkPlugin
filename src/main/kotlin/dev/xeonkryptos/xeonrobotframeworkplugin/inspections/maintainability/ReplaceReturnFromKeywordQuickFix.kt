package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceReturnFromKeywordQuickFix(keywordCall: RobotKeywordCall) : AbstractReplaceKeywordQuickFix(keywordCall, RobotNames.RETURN_FROM_KEYWORD_NORMALIZED_KEYWORD_NAME) {

    override fun isAvailable(keywordCall: RobotKeywordCall): Boolean = true

    override fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall) {
        val returnValues = keywordCall.positionalArgumentList.joinToString(GlobalConstants.SUPER_SPACE) { it.text }
        val newReturnStructure = RobotElementGenerator.getInstance(project).createNewReturnStructure(returnValues)
        keywordCall.replace(newReturnStructure)
    }
}
