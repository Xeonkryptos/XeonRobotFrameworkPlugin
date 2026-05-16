package dev.xeonkryptos.xeonrobotframeworkplugin.lsp

import com.intellij.psi.tree.IElementType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes

data class MappedToken(val elementType: IElementType, val startOffset: Int, val length: Int)

object RobotVariableSubTokenizer {

    fun tokenize(text: CharSequence, baseOffset: Int): List<MappedToken> {
        if (text.length < 3) return listOf(MappedToken(RobotTypes.LITERAL_CONSTANT, baseOffset, text.length))

        val tokens = mutableListOf<MappedToken>()
        tokenizeVariable(text, baseOffset, tokens)
        return if (tokens.isEmpty()) listOf(MappedToken(RobotTypes.LITERAL_CONSTANT, baseOffset, text.length)) else tokens
    }

    private fun tokenizeVariable(text: CharSequence, baseOffset: Int, tokens: MutableList<MappedToken>) {
        if (text.length < 3) return

        val sigil = text[0]
        val startType = when (sigil) {
            '$' -> RobotTypes.SCALAR_VARIABLE_START
            '@' -> RobotTypes.LIST_VARIABLE_START
            '&' -> RobotTypes.DICT_VARIABLE_START
            '%' -> RobotTypes.ENV_VARIABLE_START
            else -> return
        }

        if (text[1] != '{') return

        tokens.add(MappedToken(startType, baseOffset, 1))
        tokens.add(MappedToken(RobotTypes.VARIABLE_LBRACE, baseOffset + 1, 1))

        if (text.length > 3 && text[2] == '{') {
            tokenizePythonExpression(text, baseOffset, tokens)
        } else {
            tokenizeVariableBody(text, baseOffset, tokens)
        }
    }

    private fun tokenizePythonExpression(text: CharSequence, baseOffset: Int, tokens: MutableList<MappedToken>) {
        tokens.add(MappedToken(RobotTypes.PYTHON_EXPRESSION_START, baseOffset + 2, 1))

        val closingIdx = findDoubleClosingBrace(text, 3)
        if (closingIdx > 3) {
            tokens.add(MappedToken(RobotTypes.PYTHON_EXPRESSION_CONTENT, baseOffset + 3, closingIdx - 3))
        }

        if (closingIdx < text.length) {
            tokens.add(MappedToken(RobotTypes.PYTHON_EXPRESSION_END, baseOffset + closingIdx, 1))
        }
        if (closingIdx + 1 < text.length) {
            tokens.add(MappedToken(RobotTypes.VARIABLE_RBRACE, baseOffset + closingIdx + 1, 1))
        }

        val afterClose = closingIdx + 2
        if (afterClose < text.length) {
            tokenizeAccessSuffix(text, baseOffset, afterClose, tokens)
        }
    }

    private fun tokenizeVariableBody(text: CharSequence, baseOffset: Int, tokens: MutableList<MappedToken>) {
        val bodyStart = 2
        val closingIdx = findClosingBrace(text, bodyStart)

        if (closingIdx > bodyStart) {
            val bodyText = text.subSequence(bodyStart, closingIdx)
            val nestedVarIdx = findNestedVariable(bodyText)

            if (nestedVarIdx >= 0) {
                if (nestedVarIdx > 0) {
                    tokens.add(MappedToken(RobotTypes.VARIABLE_BODY, baseOffset + bodyStart, nestedVarIdx))
                }
                val nestedEnd = findClosingBrace(bodyText, nestedVarIdx + 2)
                if (nestedEnd > 0) {
                    val nestedText = bodyText.subSequence(nestedVarIdx, nestedEnd + 1)
                    tokenizeVariable(nestedText, baseOffset + bodyStart + nestedVarIdx, tokens)
                    if (nestedEnd + 1 < bodyText.length) {
                        tokens.add(MappedToken(RobotTypes.VARIABLE_BODY, baseOffset + bodyStart + nestedEnd + 1, bodyText.length - nestedEnd - 1))
                    }
                } else {
                    tokens.add(MappedToken(RobotTypes.VARIABLE_BODY, baseOffset + bodyStart, closingIdx - bodyStart))
                }
            } else {
                tokens.add(MappedToken(RobotTypes.VARIABLE_BODY, baseOffset + bodyStart, closingIdx - bodyStart))
            }
        }

        if (closingIdx < text.length) {
            tokens.add(MappedToken(RobotTypes.VARIABLE_RBRACE, baseOffset + closingIdx, 1))
        }

        val afterClose = closingIdx + 1
        if (afterClose < text.length) {
            tokenizeAccessSuffix(text, baseOffset, afterClose, tokens)
        }
    }

    private fun tokenizeAccessSuffix(text: CharSequence, baseOffset: Int, startIdx: Int, tokens: MutableList<MappedToken>) {
        var idx = startIdx
        while (idx < text.length && text[idx] == '[') {
            tokens.add(MappedToken(RobotTypes.VARIABLE_ACCESS_START, baseOffset + idx, 1))
            idx++
            val closeBracket = text.indexOf(']', idx)
            if (closeBracket > idx) {
                tokens.add(MappedToken(RobotTypes.VARIABLE_INDEX_ACCESS, baseOffset + idx, closeBracket - idx))
                tokens.add(MappedToken(RobotTypes.VARIABLE_ACCESS_END, baseOffset + closeBracket, 1))
                idx = closeBracket + 1
            } else {
                break
            }
        }
    }

    private fun findClosingBrace(text: CharSequence, startIdx: Int): Int {
        var depth = 1
        var i = startIdx
        while (i < text.length) {
            when (text[i]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) return i
                }
            }
            i++
        }
        return text.length
    }

    private fun findDoubleClosingBrace(text: CharSequence, startIdx: Int): Int {
        var i = startIdx
        while (i + 1 < text.length) {
            if (text[i] == '}' && text[i + 1] == '}') return i
            i++
        }
        return text.length
    }

    private fun findNestedVariable(text: CharSequence): Int {
        for (i in 0 until text.length - 1) {
            if (text[i] in "$@&%" && i + 1 < text.length && text[i + 1] == '{') return i
        }
        return -1
    }
}
