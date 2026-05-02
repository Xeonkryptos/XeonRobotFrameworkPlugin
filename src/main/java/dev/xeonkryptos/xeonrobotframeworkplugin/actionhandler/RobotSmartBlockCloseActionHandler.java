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
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptionHandlingStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGroupStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotIfStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopStructure;
import org.jetbrains.annotations.NotNull;

public class RobotSmartBlockCloseActionHandler extends EnterHandlerDelegateAdapter {

    private static final TokenSet TOKEN_TYPES = TokenSet.create(RobotTypes.IF, RobotTypes.TRY, RobotTypes.FOR, RobotTypes.WHILE, RobotTypes.GROUP);

    @SuppressWarnings("unchecked")
    private static final Class<PsiElement>[] TYPES = new Class[] { RobotIfStructure.class, RobotGroupStructure.class, RobotForLoopStructure.class, RobotWhileLoopStructure.class };

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

        PsiElement elementAt;
        do {
            elementAt = file.findElementAt(offset);
            offset--;
        } while (elementAt == null && offset >= 0);

        if (elementAt == null) {
            return Result.Continue;
        }

        PsiElement endBasedElement = PsiTreeUtil.getParentOfType(elementAt, TYPES);
        if (endBasedElement != null) {
            RobotExecutableStatement specialWrappingParent = PsiTreeUtil.getParentOfType(endBasedElement, RobotConditionalStructure.class, RobotExceptionHandlingStructure.class);
            if (specialWrappingParent != null) {
                endBasedElement = specialWrappingParent;
            }
            ASTNode endTokenNode = endBasedElement.getNode().findChildByType(RobotTypes.END);
            if (endTokenNode == null) {
                addEndTokenText(currentCaretOffset, endBasedElement, document);
            }
        } else if (TOKEN_TYPES.contains(PsiUtilCore.getElementType(elementAt))) {
            addEndTokenText(currentCaretOffset, elementAt, document);
        }
        return Result.Continue;
    }

    private void addEndTokenText(int caretOffset, PsiElement element, Document document) {
        int startOffset = element.getTextRange().getStartOffset();
        int lineNumber = document.getLineNumber(startOffset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);

        int indentationSize = startOffset - lineStartOffset;
        String indentation = " ".repeat(indentationSize);
        document.insertString(caretOffset, "\n%sEND".formatted(indentation));
    }
}
