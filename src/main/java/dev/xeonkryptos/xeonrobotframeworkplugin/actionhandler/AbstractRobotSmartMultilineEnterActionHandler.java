package dev.xeonkryptos.xeonrobotframeworkplugin.actionhandler;

import com.intellij.application.options.CodeStyle;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.formatter.RobotCodeStyleSettings;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                element = file.findElementAt(currentCaretOffset - 1);
            }
            if (element == null) {
                return Result.Continue;
            }

            if (expectedElementClass.isInstance(element)) {
                foundElement = expectedElementClass.cast(element);
                return Result.Continue;
            }

            int lineNumber = document.getLineNumber(currentCaretOffset);
            int lineStartOffset = document.getLineStartOffset(lineNumber);
            int previousLineStartOffset = -1;
            if (lineNumber > 0) {
                previousLineStartOffset = document.getLineStartOffset(lineNumber - 1);
            }

            boolean lookAtPreviousLine = false;
            while (element instanceof PsiWhiteSpace || element instanceof PsiComment) {
                if (element instanceof PsiWhiteSpace whiteSpace && whiteSpace.textMatches(GlobalConstants.CONTINUATION)) {
                    lookAtPreviousLine = true;
                }
                int textOffset = element.getTextOffset();
                if (textOffset <= lineStartOffset && (!lookAtPreviousLine || textOffset <= previousLineStartOffset) || textOffset == 0) {
                    return Result.Continue;
                }
                PsiElement currentElement = element;
                element = element.getPrevSibling();
                if (element == null) {
                    element = currentElement.getParent();
                }
            }

            foundElement = getExpectedElement(element, lineNumber, document);
        }
        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        if (foundElement != null) {
            handleSmartMultilineIndentation(file, editor);
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
        T foundElement = PsiTreeUtil.getParentOfType(element, expectedElementClass, false);
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
                return codePoint == '.' && i + 2 < lineText.length() && GlobalConstants.CONTINUATION.equals(lineText.substring(i, i + 3));
            }
        }
        return false;
    }

    private void handleSmartMultilineIndentation(@NotNull PsiFile file, @NotNull Editor editor) {
        int caretOffset = editor.getCaretModel().getOffset();
        Document document = editor.getDocument();
        int lineNumber = document.getLineNumber(caretOffset) - 1;
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);
        String originalText = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        String text = originalText.replace(GlobalConstants.CONTINUATION, ""); // Remove existing ellipses to identify whitespaces easier
        if (text.isBlank()) {
            // Don't add ellipsis if the previous line didn't contain anything besides whitespace. Furthermore, remove the empty line with the
            // unnecessary ellipsis to allow for a better writing flow
            if (originalText.length() != text.length()) { // Using a length check to improve performance minimally -> a contains would work, too
                int newLineStartOffset = document.getLineStartOffset(lineNumber + 1);
                document.deleteString(lineStartOffset, newLineStartOffset);
            }
        } else {
            addEllipsisAndIndentationIntoNewLine(file, editor);
        }
    }

    private void addEllipsisAndIndentationIntoNewLine(@NotNull PsiFile file, @NotNull Editor editor) {
        String textToInsert = GlobalConstants.CONTINUATION;
        RobotCodeStyleSettings customSettings = CodeStyle.getCustomSettings(file, RobotCodeStyleSettings.class);
        String whitespacesToInsert = StringUtil.repeatSymbol(' ', customSettings.AFTER_CONTINUATION_INDENT_SIZE);
        Document document = editor.getDocument();
        int caretOffset = editor.getCaretModel().getOffset();

        textToInsert = textToInsert + whitespacesToInsert;
        document.insertString(caretOffset, textToInsert);
        editor.getCaretModel().moveToOffset(caretOffset + textToInsert.length());
    }
}
