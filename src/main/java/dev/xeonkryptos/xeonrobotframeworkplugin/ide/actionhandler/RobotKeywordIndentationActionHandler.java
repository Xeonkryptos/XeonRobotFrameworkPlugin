package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RobotKeywordIndentationActionHandler extends EnterHandlerDelegateAdapter {

    private static final List<String> INTERACTION_WORDS = List.of("IF", "ELSE IF", "ELSE", "FOR", "WHILE", "TRY", "EXCEPT", "FINALLY");

    private boolean addDefaultIndentation;

    @Override
    public Result preprocessEnter(@NotNull PsiFile file,
                                  @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset,
                                  @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext,
                                  EditorActionHandler originalHandler) {
        addDefaultIndentation = false;

        int currentCaretOffset = editor.getCaretModel().getOffset();
        Document document = editor.getDocument();
        int lineNumber = document.getLineNumber(currentCaretOffset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        String lineText = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        for (int i = 0; i < lineText.length(); i++) {
            if (!Character.isWhitespace(lineText.codePointAt(i))) {
                String lineTextUpperCase = lineText.toUpperCase();
                for (String interactionWord : INTERACTION_WORDS) {
                    if (lineTextUpperCase.startsWith(interactionWord, i)) {
                        addDefaultIndentation = Character.isWhitespace(lineTextUpperCase.codePointAt(i + interactionWord.length()));
                        break;
                    }
                }
                break;
            }
        }
        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        if (addDefaultIndentation) {
            int caretOffset = editor.getCaretModel().getOffset();
            editor.getDocument().insertString(caretOffset, GlobalConstants.DEFAULT_INDENTATION);
        }
        return Result.Continue;
    }
}
