package dev.xeonkryptos.xeonrobotframeworkplugin.actionhandler;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractRobotSmartMultilineEnterActionHandler<T extends PsiElement> extends EnterHandlerDelegateAdapter {

    private final Class<T> expectedElementClass;

    @Nullable
    private T foundElement;

    protected AbstractRobotSmartMultilineEnterActionHandler(Class<T> expectedElementClass) {
        this.expectedElementClass = expectedElementClass;
    }

    @Override
    public Result preprocessEnter(@NotNull PsiFile file,
                                  @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset,
                                  @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext,
                                  EditorActionHandler originalHandler) {
        FileType fileType = file.getFileType();
        if (fileType == RobotFeatureFileType.getInstance() || fileType == RobotResourceFileType.getInstance()) {
            RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(file.getProject());
            if (!robotOptionsProvider.multilineIndentation()) {
                return Result.Continue;
            }

            Integer currentCaretOffset = caretOffset.get();

            Document document = editor.getDocument();
            // Committing document changes before to be able to search on the PSI tree
            PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);

            PsiElement element = file.findElementAt(currentCaretOffset);
            if (element == null) {
                return Result.Continue;
            }

            if (expectedElementClass.isInstance(element)) {
                foundElement = expectedElementClass.cast(element);
                return super.preprocessEnter(file, editor, caretOffset, caretAdvance, dataContext, originalHandler);
            }

            int lineNumber = document.getLineNumber(currentCaretOffset);
            int lineStartOffset = document.getLineStartOffset(lineNumber);
            int previousLineStartOffset = -1;
            if (lineNumber > 0) {
                previousLineStartOffset = document.getLineStartOffset(lineNumber - 1);
            }

            boolean lookAtPreviousLine = false;
            while (element instanceof PsiWhiteSpace || element instanceof PsiComment) {
                if (element instanceof PsiWhiteSpace whiteSpace && whiteSpace.getText().contains(GlobalConstants.ELLIPSIS)) {
                    lookAtPreviousLine = true;
                }
                int textOffset = element.getTextOffset();
                if (textOffset <= lineStartOffset && (!lookAtPreviousLine || textOffset <= previousLineStartOffset) || textOffset == 0) {
                    return Result.Continue;
                }
                element = file.findElementAt(textOffset - 1);
            }

            foundElement = getExpectedElement(element, lineNumber, document);
        }
        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        if (foundElement != null) {
            handleSmartMultilineIndentation(file, editor, foundElement);
            foundElement = null;
            return Result.Stop;
        }
        return Result.Continue;
    }

    @Nullable
    protected T getExpectedElement(@Nullable PsiElement element, int previousLine, Document document) {
        if (expectedElementClass.isInstance(element)) {
            return expectedElementClass.cast(element);
        }
        T foundElement = PsiTreeUtil.getParentOfType(element, expectedElementClass, false, RobotQualifiedNameOwner.class);
        if (foundElement != null) {
            int textOffset = foundElement.getTextOffset();
            int keywordCallLineNumber = document.getLineNumber(textOffset);
            if (keywordCallLineNumber == previousLine) {
                return foundElement;
            }

            int lineStartOffset = document.getLineStartOffset(previousLine);
            int lineEndOffset = document.getLineEndOffset(previousLine);
            TextRange lineTextRange = new TextRange(lineStartOffset, lineEndOffset);
            if (isLineStartingWithEllipsis(lineTextRange, document)) {
                return foundElement;
            }
        }
        return null;
    }

    protected boolean isLineStartingWithEllipsis(TextRange lineTextRange, Document document) {
        String lineText = document.getText(lineTextRange);
        for (int i = 0; i < lineText.length(); i++) {
            int codePoint = lineText.codePointAt(i);
            if (!Character.isWhitespace(codePoint)) {
                return codePoint == '.' && i + 2 < lineText.length() && GlobalConstants.ELLIPSIS.equals(lineText.substring(i, i + 3));
            }
        }
        return false;
    }

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
        while ((elementForIndentation instanceof PsiWhiteSpace || elementForIndentation instanceof PsiComment)
               && elementForIndentation.getTextOffset() <= lineEndOffset) {
            elementForIndentation = elementForIndentation.getNextSibling();
        }

        if (elementForIndentation != null && !(elementForIndentation instanceof PsiWhiteSpace || elementForIndentation instanceof PsiComment)) {
            int argumentForIndentationOffset = elementForIndentation.getTextOffset();
            PsiElement ellipsisElement = elementForIndentation;
            do {
                ellipsisElement = ellipsisElement.getPrevSibling();
            } while (ellipsisElement instanceof PsiWhiteSpace && !ellipsisElement.textMatches(GlobalConstants.ELLIPSIS)
                     || ellipsisElement instanceof PsiComment);

            if (ellipsisElement != null && ellipsisElement.textMatches(GlobalConstants.ELLIPSIS)) {
                int ellipsesEndOffset = ellipsisElement.getTextOffset() + ellipsisElement.getTextLength();
                whitespacesToInsert = IntStream.range(0, argumentForIndentationOffset - ellipsesEndOffset).mapToObj(n -> " ").collect(Collectors.joining());
            }
        }
        return whitespacesToInsert;
    }
}
