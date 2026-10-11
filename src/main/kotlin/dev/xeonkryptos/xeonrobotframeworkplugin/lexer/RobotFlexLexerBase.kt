package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.lexer.FlexLexer
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import java.util.ArrayDeque

abstract class RobotFlexLexerBase : FlexLexer {

    protected var currentIndex: Int = -1

    protected val previousStates: IntArray = IntArray(20)

    private class MethodArgumentContext {
        var depth = 0
        var quote = NO_QUOTE
        var hasVariable = false
    }

    private val methodArgumentContexts = ArrayDeque<MethodArgumentContext>()

    protected fun enterMethodArguments() {
        methodArgumentContexts.push(MethodArgumentContext())
    }

    protected fun leaveMethodArguments() {
        methodArgumentContexts.pop()
    }

    protected fun markVariableInMethodArgument() {
        methodArgumentContexts.peek().hasVariable = true
    }

    /**
     * Splits the remaining line into the next token of a method call argument list. Handles string literals, nested brackets, escapes and robot variables
     * (also inside strings and brackets). A comma or closing parenthesis only ends an argument when it is neither quoted nor nested.
     */
    protected fun scanMethodArgument(): IElementType {
        val ctx = methodArgumentContexts.peek()
        val length = yylength()

        if (ctx.quote == NO_QUOTE && ctx.depth == 0 && isWhitespace(yycharat(0))) {
            var whitespaceEnd = 1
            while (whitespaceEnd < length && isWhitespace(yycharat(whitespaceEnd))) whitespaceEnd++
            yypushback(length - whitespaceEnd)
            return TokenType.WHITE_SPACE
        }

        var end = findMethodArgumentEnd(ctx, length)
        if (end == 0) return finishMethodCallDelimiter(ctx, length)

        val endsAtVariable = end < length && isVariableSigil(yycharat(end))
        if (end < length && !endsAtVariable) {
            // Whitespace in front of the delimiter doesn't belong to the argument
            while (isWhitespace(yycharat(end - 1))) end--
        }
        yypushback(length - end)
        return if (endsAtVariable || ctx.hasVariable) RobotTypes.METHOD_CALL_ARGUMENT_PART else RobotTypes.METHOD_CALL_ARGUMENT
    }

    /** Returns the index of the first char not belonging to the current argument: a robot variable start or a delimiter (`,` or `)`) on top level. */
    private fun findMethodArgumentEnd(ctx: MethodArgumentContext, length: Int): Int {
        var i = 0
        while (i < length) {
            val c = yycharat(i)
            when {
                c == '\\' -> i++ // skip the escaped char
                isVariableSigil(c) && i + 1 < length && yycharat(i + 1) == '{' -> return i
                ctx.quote != NO_QUOTE -> if (c == ctx.quote) ctx.quote = NO_QUOTE
                c == '\'' || c == '"' -> ctx.quote = c
                c == '(' || c == '[' || c == '{' -> ctx.depth++
                (c == ')' || c == ',') && ctx.depth == 0 -> return i
                c == ')' || c == ']' || c == '}' -> if (ctx.depth > 0) ctx.depth--
            }
            i++
        }
        return length
    }

    private fun finishMethodCallDelimiter(ctx: MethodArgumentContext, length: Int): IElementType {
        val delimiter = yycharat(0)
        yypushback(length - 1)
        if (delimiter == ',') {
            ctx.hasVariable = false
            return RobotTypes.METHOD_CALL_ARGUMENT_COMMA
        }
        leaveMethodArguments()
        leaveState()
        return RobotTypes.METHOD_CALL_RBRACE
    }

    private fun isVariableSigil(c: Char): Boolean = c == '$' || c == '@' || c == '&' || c == '%'

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

    protected fun leaveState() {
        if (currentIndex >= 0) {
            val previousState = previousStates[currentIndex]
            --currentIndex
            yybegin(previousState)
        } else {
            yybegin(0) // 0 => YYINITIAL
        }
    }

    protected abstract fun yylength(): Int

    protected abstract fun yycharat(position: Int): Char

    protected abstract fun yypushback(numberOfChars: Int)

    private companion object {
        const val NO_QUOTE = '\u0000'
    }
}
