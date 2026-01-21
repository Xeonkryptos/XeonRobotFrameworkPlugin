package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.FlexLexer;

public abstract class AbstractRobotLexer implements FlexLexer {

    protected void pushBackEverythingExceptLeadingWhitespace() {
        int textLength = yylength();
        if (textLength > 0) {
            int leadingWhitespaceLength = computeLeadingWhitespaceLength();
            int charsToPushBack = textLength - leadingWhitespaceLength;
            if (charsToPushBack > 0) {
                yypushback(charsToPushBack);
            }
        }
    }

    protected int computeLeadingWhitespaceLength() {
        int length = 0;
        int textLength = yylength();
        for (int i = 0; i < textLength; i++) {
            char c = yycharat(i);
            if (isWhitespace(c)) {
                length++;
            } else {
                break;
            }
        }
        return length;
    }

    protected void pushBackTrailingWhitespace() {
        int textLength = yylength();
        if (textLength > 0) {
            int trailingWhitespaceLength = computeTrailingWhitespaceLength();
            if (trailingWhitespaceLength > 0) {
                yypushback(trailingWhitespaceLength);
            }
        }
    }

    protected int computeTrailingWhitespaceLength() {
        int length = 0;
        int end = yylength() - 1;
        for (int i = end; i >= 0; i--) {
            char c = yycharat(i);
            if (isWhitespace(c)) {
                length++;
            } else {
                break;
            }
        }
        return length;
    }

    protected boolean isWhitespace(char character) {
        return character == ' ' || character == '\t' || character == '\r' || character == '\n' || character == '\u00A0';
    }

    protected int indexOf(char character) {
        int length = yylength();
        for (int i = 0; i < length; i++) {
            if (yycharat(i) == character) {
                return i;
            }
        }
        return -1;
    }

    protected abstract int yylength();

    protected abstract char yycharat(int position);

    protected abstract void yypushback(int numberOfChars);
}
