package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile

/**
 * Opens the completion popup as soon as the access operator is typed within a variable to propose the members of the accessed object of the extended
 * variable syntax, e.g. `${OBJECT.<caret>}`.
 */
class RobotExtendedVariableTypedHandler : TypedHandlerDelegate() {

    override fun checkAutoPopup(charTyped: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (charTyped != '.' || file !is RobotFile || !isWithinVariable(editor)) return Result.CONTINUE

        AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
        return Result.STOP
    }

    /** Works on the document text as the PSI isn't committed yet while typing. */
    private fun isWithinVariable(editor: Editor): Boolean {
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        val lineStartOffset = document.getLineStartOffset(document.getLineNumber(caretOffset))
        val textBeforeCaret = document.charsSequence.subSequence(lineStartOffset, caretOffset)

        var openVariables = 0
        for (i in textBeforeCaret.indices) {
            val c = textBeforeCaret[i]
            if (c == '{' && i > 0 && textBeforeCaret[i - 1] in VARIABLE_SIGILS) {
                openVariables++
            } else if (c == '}' && openVariables > 0) {
                openVariables--
            }
        }
        return openVariables > 0
    }

    private companion object {
        const val VARIABLE_SIGILS = "$@&%"
    }
}
