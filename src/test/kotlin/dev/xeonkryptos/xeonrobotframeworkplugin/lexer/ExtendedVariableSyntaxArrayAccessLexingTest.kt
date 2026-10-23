package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.psi.TokenType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Verifies the tokens of array accesses within the extended variable syntax, e.g. `${Partner.bankverbindung[0].iban}`. The content of an access is split into index, slice and
 * literal tokens by a second lexer layer, so it is a single `EXTENDED_VARIABLE_ACCESS_BODY` here.
 */
class ExtendedVariableSyntaxArrayAccessLexingTest : BasePlatformTestCase() {

    private fun lex(expression: String): List<Pair<String, String>> {
        val text = "*** Test Cases ***\nT\n    Log    $expression\n"
        val lexer = FlexAdapter(RobotLexerExtension(project))
        lexer.start(text)
        val tokens = mutableListOf<Pair<String, String>>()
        while (lexer.tokenType != null) {
            val type = lexer.tokenType!!
            if (type != TokenType.WHITE_SPACE) tokens += type.toString() to text.substring(lexer.tokenStart, lexer.tokenEnd)
            lexer.advance()
        }
        val keywordIndex = tokens.indexOfFirst { it.first == "KEYWORD_NAME" }
        return tokens.drop(keywordIndex + 1).takeWhile { it.first != "EOL" }
    }

    fun `test index access in attribute chain`() {
        val tokens = lex("\${Partner.bankverbindung[0].iban}")
        assertEquals(listOf("\$", "{", "Partner", ".", "bankverbindung", "[", "0", "]", ".", "iban", "}"), tokens.map { it.second })
        assertEquals(
            listOf(
                "SCALAR_VARIABLE_START", "VARIABLE_LBRACE", "VARIABLE_BODY", "DOT_OPERATOR", "VARIABLE_BODY", "VARIABLE_ACCESS_START", "EXTENDED_VARIABLE_ACCESS_BODY",
                "VARIABLE_ACCESS_END", "DOT_OPERATOR", "VARIABLE_BODY", "VARIABLE_RBRACE"
            ), tokens.map { it.first })
    }

    fun `test index access behind method call`() {
        val tokens = lex("\${Partner.getBankverbindungen()[0].iban}")
        assertEquals(
            listOf("\$", "{", "Partner", ".", "getBankverbindungen", "(", ")", "[", "0", "]", ".", "iban", "}"), tokens.map { it.second })
        assertEquals("VARIABLE_BODY_METHOD_CALL_NAME", tokens[4].first)
        assertEquals("VARIABLE_ACCESS_START", tokens[7].first)
        assertEquals("EXTENDED_VARIABLE_ACCESS_BODY", tokens[8].first)
    }

    fun `test slice access`() {
        assertEquals("EXTENDED_VARIABLE_ACCESS_BODY", lex("\${a.b[1:2].c}")[6].first)
    }

    fun `test access with key`() {
        val tokens = lex("\${a.b['key'].c}")
        assertEquals(listOf("[", "'key'", "]"), tokens.subList(5, 8).map { it.second })
        assertEquals("EXTENDED_VARIABLE_ACCESS_BODY", tokens[6].first)
    }

    fun `test access with variable`() {
        val tokens = lex("\${a.b[\${i}].c}")
        assertEquals(listOf("\$", "{", "a", ".", "b", "[", "\$", "{", "i", "}", "]", ".", "c", "}"), tokens.map { it.second })
    }

    fun `test multiple accesses`() {
        val tokens = lex("\${a.b[0][1].c}")
        assertEquals(listOf("[", "0", "]", "[", "1", "]"), tokens.subList(5, 11).map { it.second })
    }

    fun `test access directly at end`() {
        val tokens = lex("\${a.b[0]}")
        assertEquals(listOf("a", ".", "b", "[", "0", "]", "}"), tokens.drop(2).map { it.second })
    }

    fun `test item access behind the variable is not touched`() {
        val tokens = lex("\${a.b}[0]")
        assertEquals(listOf("}", "[", "0", "]"), tokens.drop(5).map { it.second })
    }

    fun `test trailing access operator without member`() {
        val tokens = lex("\${Partner.}")
        assertEquals(listOf("SCALAR_VARIABLE_START", "VARIABLE_LBRACE", "VARIABLE_BODY", "DOT_OPERATOR", "VARIABLE_RBRACE"), tokens.map { it.first })
        assertEquals(listOf("Partner", "."), tokens.subList(2, 4).map { it.second })
    }
}
