package dev.xeonkryptos.xeonrobotframeworkplugin.actionhandler;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElseIfStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElseStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFinallyStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotIfStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTryStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordIndentationActionHandler extends EnterHandlerDelegateAdapter {

    private static final TokenSet TOKEN_TYPES = TokenSet.create(RobotTypes.IF,
                                                                RobotTypes.ELSE_IF,
                                                                RobotTypes.ELSE,
                                                                RobotTypes.FOR,
                                                                RobotTypes.WHILE,
                                                                RobotTypes.TRY,
                                                                RobotTypes.EXCEPT,
                                                                RobotTypes.FINALLY);
    @SuppressWarnings("unchecked")
    private static final Class<PsiElement>[] TYPES = new Class[] { RobotIfStructure.class,
                                                                   RobotElseIfStructure.class,
                                                                   RobotElseStructure.class,
                                                                   RobotForLoopStructure.class,
                                                                   RobotWhileLoopStructure.class,
                                                                   RobotTryStructure.class,
                                                                   RobotExceptStructure.class,
                                                                   RobotFinallyStructure.class };

    private boolean addDefaultIndentation;

    @Override
    public Result preprocessEnter(@NotNull PsiFile file,
                                  @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset,
                                  @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext,
                                  EditorActionHandler originalHandler) {
        addDefaultIndentation = false;

        Document document = editor.getDocument();
        PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);

        PsiElement elementAt;
        int currentCaretOffset = editor.getCaretModel().getOffset();
        do {
            elementAt = file.findElementAt(currentCaretOffset);
            currentCaretOffset--;
        } while (elementAt == null && currentCaretOffset > 0);
        ++currentCaretOffset;

        if (elementAt == null) {
            return Result.Continue;
        }

        PsiElement parentElement = PsiTreeUtil.getParentOfType(elementAt, TYPES);
        if (parentElement != null) {
            PsiElement firstChild = parentElement.getFirstChild();
            if (firstChild != null) {
                updateDefaultIndentationAddFlag(currentCaretOffset, firstChild, document);
            }
        } else if (TOKEN_TYPES.contains(elementAt.getNode().getElementType())) {
            updateDefaultIndentationAddFlag(currentCaretOffset, elementAt, document);
        }
        return Result.Continue;
    }

    private void updateDefaultIndentationAddFlag(int caretOffset, PsiElement element, Document document) {
        TextRange textRange = element.getTextRange();
        int lineNumberOfFirstChild = document.getLineNumber(textRange.getStartOffset());
        int lineNumberOfCaret = document.getLineNumber(caretOffset);
        addDefaultIndentation = lineNumberOfFirstChild == lineNumberOfCaret;
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
