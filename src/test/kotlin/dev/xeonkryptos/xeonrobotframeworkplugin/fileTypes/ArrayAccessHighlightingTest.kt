package dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes

import com.intellij.psi.TokenType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** Verifies the highlighting of array accesses in the extended variable syntax, e.g. `${Partner.bankverbindung[0].iban}`. */
class ArrayAccessHighlightingTest : BasePlatformTestCase() {

    private fun highlighted(expression: String): Map<String, String> {
        val text = "*** Test Cases ***\nT\n    Log    $expression\n"
        val highlighter = RobotHighlighter(project)
        val lexer = highlighter.highlightingLexer
        lexer.start(text)
        val result = linkedMapOf<String, String>()
        while (lexer.tokenType != null) {
            val type = lexer.tokenType!!
            if (type != TokenType.WHITE_SPACE) {
                result.putIfAbsent(text.substring(lexer.tokenStart, lexer.tokenEnd), highlighter.getTokenHighlights(type).joinToString { it.externalName })
            }
            lexer.advance()
        }
        return result
    }

    fun `test index access`() {
        val tokens = highlighted("\${Partner.bankverbindung[0].iban}")
        assertEquals("ROBOT_EXTENDED_VARIABLE_ACCESS_BRACKETS", tokens["["])
        assertEquals("ROBOT_EXTENDED_VARIABLE_ACCESS_BRACKETS", tokens["]"])
        assertEquals("ROBOT_VARIABLE_ARRAY_ACCESS_INDEX", tokens["0"])
    }

    fun `test slice access`() = assertEquals("ROBOT_VARIABLE_ARRAY_ACCESS_INDEX", highlighted("\${a.b[1:2].c}")["1:2"])
}
