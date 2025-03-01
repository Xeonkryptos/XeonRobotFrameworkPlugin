package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RobotEnterActionHandler extends EnterHandlerDelegateAdapter {

    private static final Set<String> INTERACTION_WORDS = new HashSet<>(Arrays.asList("IF", "ELSE", "ELSE IF", "FOR", "WHILE", "TRY", "EXCEPT", "FINALLY"));

    @Override
    public Result postProcessEnter(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull DataContext context) {
        if (psiFile instanceof RobotFile) {
            int caretOffset = editor.getCaretModel().getOffset();
            Document document = editor.getDocument();
            int lineNumberOffset = document.getLineNumber(caretOffset) - 1;
            int lineStartOffset = document.getLineStartOffset(lineNumberOffset);
            int lineEndOffset = document.getLineEndOffset(lineNumberOffset);
            List<PsiElement> results = new ArrayList<>();

            for (int i = lineStartOffset; i <= lineEndOffset; i++) {
                PsiElement element = psiFile.findElementAt(i);
                if (!(element instanceof PsiWhiteSpace) && !results.contains(element)) {
                    results.add(element);
                }
            }

            if (!results.isEmpty()) {
                PsiElement element = results.get(0);
                IElementType type = element.getNode().getElementType();
                if (!results.isEmpty() &&
                    (type == RobotTokenTypes.KEYWORD_DEFINITION || type == RobotTokenTypes.SYNTAX_MARKER && INTERACTION_WORDS.contains(element.getText()))) {
                    document.insertString(caretOffset, "    ");
                }
            }
        }
        return Result.Continue;
    }
}
