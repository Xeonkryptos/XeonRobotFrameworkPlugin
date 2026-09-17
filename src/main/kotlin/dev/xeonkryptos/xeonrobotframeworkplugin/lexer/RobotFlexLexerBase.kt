package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.lexer.FlexLexer

abstract class RobotFlexLexerBase : FlexLexer {

    protected fun pushBackEverythingExceptLeadingWhitespace() {
        val textLength = yylength()
        if (textLength > 0) {
            val leadingWhitespaceLength = computeLeadingWhitespaceLength()
            val charsToPushBack = textLength - leadingWhitespaceLength
            if (charsToPushBack > 0) {
                yypushback(charsToPushBack)
            }
        }
    }

    protected fun computeLeadingWhitespaceLength(): Int {
        var length = 0
        val textLength = yylength()
        for (i in 0..<textLength) {
            val c = yycharat(i)
            if (isWhitespace(c)) {
                length++
            } else {
                break
            }
        }
        return length
    }

    protected fun pushBackTrailingWhitespace() {
        val textLength = yylength()
        if (textLength > 0) {
            val trailingWhitespaceLength = computeTrailingWhitespaceLength()
            if (trailingWhitespaceLength > 0) {
                yypushback(trailingWhitespaceLength)
            }
        }
    }

    protected fun computeTrailingWhitespaceLength(): Int {
        var length = 0
        val end = yylength() - 1
        for (i in end downTo 0) {
            val c = yycharat(i)
            if (isWhitespace(c)) {
                length++
            } else {
                break
            }
        }
        return length
    }

    protected fun isWhitespace(character: Char): Boolean {
        return character == ' ' || character == '\t' || character == '\r' || character == '\n' || character == '\u00A0'
    }

    protected fun indexOf(character: Char): Int {
        val length = yylength()
        for (i in 0..<length) {
            if (yycharat(i) == character) {
                return i
            }
        }
        return -1
    }

    protected abstract fun yylength(): Int

    protected abstract fun yycharat(position: Int): Char

    protected abstract fun yypushback(numberOfChars: Int)
}
