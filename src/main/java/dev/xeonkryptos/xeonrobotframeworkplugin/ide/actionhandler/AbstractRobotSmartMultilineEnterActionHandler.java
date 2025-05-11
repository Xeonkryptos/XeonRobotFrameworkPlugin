package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractRobotSmartMultilineEnterActionHandler<T extends PsiElement> extends EnterHandlerDelegateAdapter {

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        FileType fileType = file.getFileType();
        if (fileType == RobotFeatureFileType.getInstance() || fileType == RobotResourceFileType.getInstance()) {
            RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(file.getProject());
            if (!robotOptionsProvider.multilineIndentation()) {
                return Result.Continue;
            }

            Document document = editor.getDocument();
            // Committing document changes before to be able to search on the PSI tree
            PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);

            int caretOffset = editor.getCaretModel().getOffset();
            PsiElement currentElement = file.findElementAt(caretOffset);
            while (currentElement instanceof PsiWhiteSpace) {
                currentElement = currentElement.getPrevSibling();
            }
            if (currentElement == null) {
                return Result.Continue;
            }
            int elementTextOffset = currentElement.getTextOffset();
            int lineNumber = document.getLineNumber(elementTextOffset);
            int lineStartOffset = document.getLineStartOffset(lineNumber);
            T element = getExpectedElement(currentElement, lineStartOffset);
            if (element != null && isMultilineSupportedFor(element)) {
                handleSmartMultilineIndentation(file, editor, element);
            }
        }
        return Result.Continue;
    }

    @Nullable
    protected abstract T getExpectedElement(@Nullable PsiElement element, int lineStartOffset);

    protected abstract boolean isMultilineSupportedFor(@NotNull T element);

    private void handleSmartMultilineIndentation(@NotNull PsiFile file, @NotNull Editor editor, T multilineElement) {
        int caretOffset = editor.getCaretModel().getOffset();
        Document document = editor.getDocument();
        int lineNumber = document.getLineNumber(caretOffset) - 1;
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        String originalText = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        String text = originalText.replace(GlobalConstants.ELLIPSIS, ""); // Remove existing ellipses to identify whitespaces easier
        if (text.isBlank()) {
            // Don't add ellipsis if the previous line didn't contain anything besides whitespace. Furthermore, remove the empty line with the
            // unnecessary ellipsis to allow for a better writing flow
            if (originalText.length() != text.length()) { // Using a length check to improve performance minimally -> a contains would work, too
                int newLineStartOffset = document.getLineStartOffset(lineNumber + 1);
                document.deleteString(lineStartOffset, newLineStartOffset);
            }
        } else {
            addEllipsisAndIndentationIntoNewLine(file, editor, multilineElement, lineNumber);
        }
    }

    private void addEllipsisAndIndentationIntoNewLine(@NotNull PsiFile file, @NotNull Editor editor, T multilinePsiElement, int lineNumber) {
        String textToInsert = GlobalConstants.ELLIPSIS;
        String whitespacesToInsert = GlobalConstants.DEFAULT_INDENTATION;
        Document document = editor.getDocument();
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        int bracketSettingTextOffset = multilinePsiElement.getTextOffset();
        int bracketSettingLineNumber = document.getLineNumber(bracketSettingTextOffset);
        int caretOffset = editor.getCaretModel().getOffset();
        if (lineNumber != bracketSettingLineNumber) {
            // When there is already a text in a new line after the bracket setting definition, extract the indentation from there by finding the argument
            // of that line and its start offset and the diff to the ellipsis. This is then to be used as indentation for after the ellipsis.
            whitespacesToInsert = evaluateCustomIndentationBasedOnFirstNonWhitespaceElement(file, lineStartOffset, lineEndOffset);
        }

        textToInsert = textToInsert + whitespacesToInsert;
        document.insertString(caretOffset, textToInsert);
        editor.getCaretModel().moveToOffset(caretOffset + textToInsert.length());
    }

    private String evaluateCustomIndentationBasedOnFirstNonWhitespaceElement(@NotNull PsiFile file, int lineStartOffset, int lineEndOffset) {
        String whitespacesToInsert = GlobalConstants.DEFAULT_INDENTATION;
        PsiElement elementForIndentation = file.findElementAt(lineStartOffset);
        while (elementForIndentation instanceof PsiWhiteSpace && elementForIndentation.getTextOffset() <= lineEndOffset) {
            elementForIndentation = elementForIndentation.getNextSibling();
        }

        if (elementForIndentation != null && !(elementForIndentation instanceof PsiWhiteSpace)) {
            int argumentForIndentationOffset = elementForIndentation.getTextOffset();
            PsiElement ellipsisElement = elementForIndentation;
            do {
                ellipsisElement = ellipsisElement.getPrevSibling();
            } while (ellipsisElement instanceof PsiWhiteSpace && !ellipsisElement.textMatches(GlobalConstants.ELLIPSIS));

            if (ellipsisElement != null && ellipsisElement.textMatches(GlobalConstants.ELLIPSIS)) {
                int ellipsesEndOffset = ellipsisElement.getTextOffset() + ellipsisElement.getTextLength();
                whitespacesToInsert = IntStream.range(0, argumentForIndentationOffset - ellipsesEndOffset).mapToObj(n -> " ").collect(Collectors.joining());
            }
        }
        return whitespacesToInsert;
    }
}
