package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

class ReplaceContinueForLoopQuickFix(keywordCall: RobotKeywordCall) : AbstractReplaceKeywordQuickFix(keywordCall, RobotNames.CONTINUE_FOR_LOOP_NORMALIZED_KEYWORD_NAME) {

    override fun isAvailable(keywordCall: RobotKeywordCall): Boolean = true

    override fun invoke(project: Project, file: PsiFile, keywordCall: RobotKeywordCall) {
        val loopControlStructure = RobotElementGenerator.getInstance(project).createNewLoopControlStructure(RobotElementGenerator.LoopControlStructureType.CONTINUE)
        keywordCall.replace(loopControlStructure)
    }
}
