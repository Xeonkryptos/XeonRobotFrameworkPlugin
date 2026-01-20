package dev.xeonkryptos.xeonrobotframeworkplugin.actionhandler;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGroupStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotIfStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopStructure;
import org.jetbrains.annotations.NotNull;

public class RobotSmartBlockCloseActionHandler extends EnterHandlerDelegateAdapter {

    @Override
    public Result preprocessEnter(@NotNull PsiFile file,
                                  @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset,
                                  @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext,
                                  EditorActionHandler originalHandler) {
        Document document = editor.getDocument();
        PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
        int currentCaretOffset = editor.getCaretModel().getOffset();
        int offset = currentCaretOffset;
        PsiElement elementAt = file.findElementAt(offset);
        while (elementAt == null) {
            offset--;
            if (offset < 0) {
                return Result.Continue;
            }
            elementAt = file.findElementAt(offset);
        }
        PsiElement endBasedElement = PsiTreeUtil.getParentOfType(elementAt, RobotIfStructure.class, RobotGroupStructure.class, RobotForLoopStructure.class, RobotWhileLoopStructure.class);
        if (endBasedElement != null) {
            ASTNode endTokenNode = endBasedElement.getNode().findChildByType(RobotTypes.END);
            if (endTokenNode == null) {
                int startOffset = endBasedElement.getTextRange().getStartOffset();
                int lineNumber = document.getLineNumber(startOffset);
                int lineStartOffset = document.getLineStartOffset(lineNumber);

                int indentationSize = startOffset - lineStartOffset;
                String indentation = " ".repeat(indentationSize);
                document.insertString(currentCaretOffset, "\n%sEND".formatted(indentation));
            }
        }
        return Result.Continue;
    }
}
