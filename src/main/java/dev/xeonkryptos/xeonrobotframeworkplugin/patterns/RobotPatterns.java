package dev.xeonkryptos.xeonrobotframeworkplugin.patterns;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class RobotPatterns {

    private static final Pattern CONTINUATION_IN_SAME_LINE_PATTERN = Pattern.compile("\\.{3} *(?!.*\\n)");

    public static <T extends PsiElement> PatternCondition<T> indented() {
        return new PatternCondition<>("indented") {
            @Override
            public boolean accepts(@NotNull T t, ProcessingContext context) {
                Document document = t.getContainingFile().getFileDocument();

                TextRange textRange = t.getTextRange();
                int startOffset = textRange.getStartOffset();

                int lineNumber = document.getLineNumber(startOffset);
                int lineStartOffset = document.getLineStartOffset(lineNumber);

                return startOffset - lineStartOffset > 0;
            }
        };
    }

    public static <T extends PsiElement> PatternCondition<T> atFirstPositionOf(ElementPattern<? extends T> pattern) {
        return new PatternCondition<>("atFirstPositionOf") {
            @Override
            public boolean accepts(@NotNull T t, ProcessingContext context) {
                PsiElement previousElement = null;
                PsiElement element = t;
                while (element != null) {
                    if (pattern.accepts(element, context)) {
                        return true;
                    }
                    if (previousElement != null) {
                        if (element.getFirstChild() != previousElement) {
                            return false;
                        }
                    }
                    previousElement = element;
                    element = element.getContext();
                }
                return false;
            }
        };
    }

    public static <T extends PsiElement> PatternCondition<T> previousNonWhitespaceOrCommentSibling(ElementPattern<? extends T> pattern) {
        return new PatternCondition<>("previousNonWhitespaceOrCommentSibling") {
            @Override
            public boolean accepts(@NotNull T t, ProcessingContext context) {
                Document document = t.getContainingFile().getFileDocument();
                TextRange textRange = t.getTextRange();
                int currentLineNumber = document.getLineNumber(textRange.getStartOffset());

                boolean lineChangeAllowed = false;
                PsiElement previousSibling = t.getPrevSibling();
                while (previousSibling != null) {
                    if (!(previousSibling instanceof PsiWhiteSpace) && !(previousSibling instanceof PsiComment)) {
                        return pattern.accepts(previousSibling, context);
                    }

                    if (previousSibling instanceof PsiWhiteSpace whiteSpace) {
                        TextRange whiteSpaceTextRange = whiteSpace.getTextRange();

                        int endOffset = whiteSpaceTextRange.getEndOffset();

                        int endLineNumber = document.getLineNumber(endOffset);

                        String whiteSpaceText = whiteSpace.getText();
                        boolean endsWithContinuationInSameLine = CONTINUATION_IN_SAME_LINE_PATTERN.matcher(whiteSpaceText).find();
                        if (!lineChangeAllowed && !endsWithContinuationInSameLine && currentLineNumber != endLineNumber) {
                            break;
                        } else if (!lineChangeAllowed && endsWithContinuationInSameLine && currentLineNumber == endLineNumber) {
                            lineChangeAllowed = true;
                        }
                    }
                    previousSibling = previousSibling.getPrevSibling();
                }
                return false;
            }
        };
    }
}
