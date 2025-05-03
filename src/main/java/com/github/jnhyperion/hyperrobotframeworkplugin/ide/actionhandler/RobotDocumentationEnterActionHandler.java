package com.github.jnhyperion.hyperrobotframeworkplugin.ide.actionhandler;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotFeatureFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotResourceFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Argument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.BracketSetting;
import com.github.jnhyperion.hyperrobotframeworkplugin.util.GlobalConstants;
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
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RobotDocumentationEnterActionHandler extends EnterHandlerDelegateAdapter {

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
            BracketSetting bracketSetting = currentElement instanceof BracketSetting ?
                                            (BracketSetting) currentElement :
                                            PsiTreeUtil.getParentOfType(currentElement, BracketSetting.class);
            if (bracketSetting != null && bracketSetting.isDocumentation()) {
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
                    return Result.Continue;
                }

                addEllipsisAndIndentationIntoNewLine(file, editor, bracketSetting, lineNumber);
            }
        }
        return Result.Continue;
    }

    private void addEllipsisAndIndentationIntoNewLine(@NotNull PsiFile file, @NotNull Editor editor, PsiElement multilinePsiElement, int lineNumber) {
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
            whitespacesToInsert = evaluateCustomIndentationBasedOnArgument(file, lineStartOffset, lineEndOffset);
        }

        textToInsert = textToInsert + whitespacesToInsert;
        document.insertString(caretOffset, textToInsert);
        editor.getCaretModel().moveToOffset(caretOffset + textToInsert.length());
    }

    private String evaluateCustomIndentationBasedOnArgument(@NotNull PsiFile file, int lineStartOffset, int lineEndOffset) {
        String whitespacesToInsert = GlobalConstants.DEFAULT_INDENTATION;
        PsiElement argumentForIndentation = file.findElementAt(lineStartOffset);
        while (argumentForIndentation != null && !(argumentForIndentation instanceof Argument) && argumentForIndentation.getTextOffset() <= lineEndOffset) {
            argumentForIndentation = argumentForIndentation.getNextSibling();
        }
        if (argumentForIndentation instanceof Argument) {
            int argumentForIndentationOffset = argumentForIndentation.getTextOffset();
            PsiElement ellipsisElement = argumentForIndentation;
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
