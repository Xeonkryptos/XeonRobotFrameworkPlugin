package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.psi.TokenType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Verifies the tokens the lexer produces for method calls in the extended variable syntax, e.g. `${OBJECT.doSomething(1234, 'text', ${var})}`.
 *
 * Arguments are rendered as `ARG <text>` (complete argument without any variable), `PART <text>` (text around robot variables), `VAR <text>` (a robot
 * variable inside an argument) and `COMMA` (argument separator). Whitespace tokens are omitted.
 */
class ExtendedVariableSyntaxMethodCallLexingTest : BasePlatformTestCase() {

    private fun lexLine(expression: String, keepWhitespace: Boolean = false): List<Pair<String, String>> {
        val text = "*** Test Cases ***\nT\n    Log    $expression\n"
        val lexer = FlexAdapter(RobotLexerExtension(project))
        lexer.start(text)
        val tokens = mutableListOf<Pair<String, String>>()
        while (lexer.tokenType != null) {
            val type = lexer.tokenType!!
            if (keepWhitespace || type != TokenType.WHITE_SPACE) tokens += type.toString() to text.substring(lexer.tokenStart, lexer.tokenEnd)
            lexer.advance()
        }
        val keywordIndex = tokens.indexOfFirst { it.first == "KEYWORD_NAME" }
        return tokens.drop(keywordIndex + 1).takeWhile { it.first != "EOL" }
    }

    /** Renders everything between the opening parenthesis of the first method call and its matching closing one. */
    private fun arguments(expression: String): List<String> {
        val tokens = lexLine(expression, keepWhitespace = true)
        val result = mutableListOf<String>()
        var index = tokens.indexOfFirst { it.first == "METHOD_CALL_LBRACE" } + 1
        assertTrue("No method call found in $expression: $tokens", index > 0)
        var nestedVariables = 0
        val variableText = StringBuilder()
        while (index < tokens.size) {
            val (type, text) = tokens[index++]
            if (nestedVariables > 0 || type.endsWith("_VARIABLE_START")) {
                variableText.append(text)
                if (type.endsWith("_VARIABLE_START")) nestedVariables++
                if (type == "VARIABLE_RBRACE" && --nestedVariables == 0) {
                    // list/dict item access directly behind the variable belongs to it, e.g. ${list}[0]
                    while (index < tokens.size && tokens[index].first in VARIABLE_ACCESS_TOKENS) variableText.append(tokens[index++].second)
                    result += "VAR $variableText"
                    variableText.clear()
                }
                continue
            }
            when (type) {
                "WHITE_SPACE" -> Unit
                "METHOD_CALL_ARGUMENT" -> result += "ARG $text"
                "METHOD_CALL_ARGUMENT_PART" -> result += "PART $text"
                "METHOD_CALL_ARGUMENT_COMMA" -> result += "COMMA"
                "METHOD_CALL_RBRACE" -> return result
                else -> fail("Unexpected token $type '$text' in arguments of $expression: $tokens")
            }
        }
        fail("Method call of $expression is not closed: $tokens")
        return result
    }

    private companion object {
        val VARIABLE_ACCESS_TOKENS = setOf("VARIABLE_ACCESS_START", "EXTENDED_VARIABLE_ACCESS_BODY", "VARIABLE_ACCESS_END")
    }

    private fun assertArguments(expression: String, vararg expected: String) = assertEquals(expression, expected.toList(), arguments(expression))

    fun `test object access without method call`() {
        val tokens = lexLine("\${OBJECT.name}")
        assertEquals(
            listOf("SCALAR_VARIABLE_START", "VARIABLE_LBRACE", "VARIABLE_BODY", "DOT_OPERATOR", "VARIABLE_BODY", "VARIABLE_RBRACE"), tokens.map { it.first })
        assertEquals(listOf("$", "{", "OBJECT", ".", "name", "}"), tokens.map { it.second })
    }

    fun `test method call name and structure tokens`() {
        val tokens = lexLine("\${OBJECT.doSomething(1234)}")
        assertEquals(
            listOf(
                "SCALAR_VARIABLE_START", "VARIABLE_LBRACE", "VARIABLE_BODY", "DOT_OPERATOR", "VARIABLE_BODY_METHOD_CALL_NAME", "METHOD_CALL_LBRACE",
                "METHOD_CALL_ARGUMENT", "METHOD_CALL_RBRACE", "VARIABLE_RBRACE"
            ), tokens.map { it.first })
        assertEquals("doSomething", tokens[4].second)
    }

    fun `test chained access`() {
        val tokens = lexLine("\${OBJECT.first(1).second.third(2, 3)}")
        assertEquals(
            listOf("OBJECT", ".", "first", "(", "1", ")", ".", "second", ".", "third", "(", "2", ",", "3", ")", "}"), tokens.drop(2).map { it.second })
    }

    fun `test no arguments`() = assertArguments("\${OBJECT.run()}")

    fun `test whitespace only arguments`() = assertArguments("\${OBJECT.run(   )}")

    fun `test single numeric argument`() = assertArguments("\${OBJECT.run(1234)}", "ARG 1234")

    fun `test multiple arguments`() = assertArguments("\${OBJECT.run(1, 2, 3)}", "ARG 1", "COMMA", "ARG 2", "COMMA", "ARG 3")

    fun `test arguments without spaces around separators`() = assertArguments("\${OBJECT.run(1,2,3)}", "ARG 1", "COMMA", "ARG 2", "COMMA", "ARG 3")

    fun `test whitespace around arguments is not part of the argument`() = assertArguments("\${OBJECT.run(  1  ,  2  )}", "ARG 1", "COMMA", "ARG 2")

    fun `test keyword argument`() = assertArguments("\${OBJECT.run(key=1, other='x')}", "ARG key=1", "COMMA", "ARG other='x'")

    fun `test python expressions as arguments`() = assertArguments(
        "\${OBJECT.run(1 + 2, -1, x if y else z, not a)}", "ARG 1 + 2", "COMMA", "ARG -1", "COMMA", "ARG x if y else z", "COMMA", "ARG not a")

    fun `test single quoted string with separators`() = assertArguments("\${OBJECT.run('a,b)', 2)}", "ARG 'a,b)'", "COMMA", "ARG 2")

    fun `test double quoted string with separators`() = assertArguments("\${OBJECT.run(\"a,b)\", 2)}", "ARG \"a,b)\"", "COMMA", "ARG 2")

    fun `test quote inside other quote type`() = assertArguments("\${OBJECT.run(\"it's, fine\", 'say \"hi\", ok')}", "ARG \"it's, fine\"", "COMMA", "ARG 'say \"hi\", ok'")

    fun `test escaped quote inside string`() = assertArguments("\${OBJECT.run('it\\'s, fine)', 2)}", "ARG 'it\\'s, fine)'", "COMMA", "ARG 2")

    fun `test escaped backslash before closing quote`() = assertArguments("\${OBJECT.run('a\\\\', 2)}", "ARG 'a\\\\'", "COMMA", "ARG 2")

    fun `test empty string argument`() = assertArguments("\${OBJECT.run('', \"\")}", "ARG ''", "COMMA", "ARG \"\"")

    fun `test nested call as argument`() = assertArguments("\${OBJECT.run(foo(1, 2), 3)}", "ARG foo(1, 2)", "COMMA", "ARG 3")

    fun `test deeply nested calls`() = assertArguments("\${OBJECT.run(a(b(c(d(e(f(1, 2), 3), 4), 5), 6), 7), 8)}", "ARG a(b(c(d(e(f(1, 2), 3), 4), 5), 6), 7)", "COMMA", "ARG 8")

    fun `test list and dict literals`() = assertArguments(
        "\${OBJECT.run([1, 2, [3, 4]], {'a': 1, 'b': {'c': 2}}, (5, 6))}", "ARG [1, 2, [3, 4]]", "COMMA", "ARG {'a': 1, 'b': {'c': 2}}", "COMMA", "ARG (5, 6)")

    fun `test brackets inside string do not affect nesting`() = assertArguments("\${OBJECT.run('(((', ')')}", "ARG '((('", "COMMA", "ARG ')'")

    fun `test subscript and attribute access in argument`() = assertArguments("\${OBJECT.run(data[0], other.attr, lst[1:2])}", "ARG data[0]", "COMMA", "ARG other.attr", "COMMA", "ARG lst[1:2]")

    fun `test sigil characters as plain text`() = assertArguments(
        "\${OBJECT.run(50%, a % b, '\$', \$, @, &, 100%)}", "ARG 50%", "COMMA", "ARG a % b", "COMMA", "ARG '$'", "COMMA", "ARG $", "COMMA", "ARG @", "COMMA", "ARG &",
        "COMMA", "ARG 100%")

    fun `test sigil directly before closing parenthesis`() = assertArguments("\${OBJECT.run(50%)}", "ARG 50%")

    fun `test sigil before opening brace which is not a variable inside string literal`() = assertArguments("\${OBJECT.run('\$ {x}')}", "ARG '$ {x}'")

    fun `test escaped variable is plain text`() = assertArguments("\${OBJECT.run(\\\${x}, 2)}", "ARG \\\${x}", "COMMA", "ARG 2")

    fun `test only a variable as argument`() = assertArguments("\${OBJECT.run(\${anotherVar})}", "VAR \${anotherVar}")

    fun `test all variable types as arguments`() = assertArguments(
        "\${OBJECT.run(\${a}, @{b}, &{c}, %{D})}", "VAR \${a}", "COMMA", "VAR @{b}", "COMMA", "VAR &{c}", "COMMA", "VAR %{D}")

    fun `test variable with prefix`() = assertArguments("\${OBJECT.run(pre\${x})}", "PART pre", "VAR \${x}")

    fun `test variable with suffix`() = assertArguments("\${OBJECT.run(\${x}suf)}", "VAR \${x}", "PART suf")

    fun `test variable with prefix and suffix`() = assertArguments("\${OBJECT.run(pre\${x}suf)}", "PART pre", "VAR \${x}", "PART suf")

    fun `test arbitrarily combined variables and text`() = assertArguments(
        "\${OBJECT.run(a\${x}b\${y}c\${z})}", "PART a", "VAR \${x}", "PART b", "VAR \${y}", "PART c", "VAR \${z}")

    fun `test directly adjacent variables`() = assertArguments("\${OBJECT.run(\${x}\${y})}", "VAR \${x}", "VAR \${y}")

    fun `test variables with python operators`() = assertArguments("\${OBJECT.run(\${x} + \${y}, \${z} * 2)}", "VAR \${x}", "PART + ", "VAR \${y}", "COMMA", "VAR \${z}", "PART * 2")

    fun `test variable inside string literal`() = assertArguments("\${OBJECT.run('\${x}')}", "PART '", "VAR \${x}", "PART '")

    fun `test variable inside string with separators around`() = assertArguments("\${OBJECT.run('a,\${x},b)', 2)}", "PART 'a,", "VAR \${x}", "PART ,b)'", "COMMA", "ARG 2")

    fun `test variable inside list literal keeps separators nested`() = assertArguments(
        "\${OBJECT.run([\${a}, 1], 2)}", "PART [", "VAR \${a}", "PART , 1]", "COMMA", "ARG 2")

    fun `test variable inside nested call keeps closing parenthesis nested`() = assertArguments(
        "\${OBJECT.run(foo(\${a}), bar(1, \${b}))}", "PART foo(", "VAR \${a}", "PART )", "COMMA", "PART bar(1, ", "VAR \${b}", "PART )")

    fun `test variable followed by separator inside brackets and top level`() = assertArguments(
        "\${OBJECT.run({'k': \${v}}, \${w})}", "PART {'k': ", "VAR \${v}", "PART }", "COMMA", "VAR \${w}")

    fun `test variable resets between arguments`() = assertArguments("\${OBJECT.run(\${a}, plain, \${b})}", "VAR \${a}", "COMMA", "ARG plain", "COMMA", "VAR \${b}")

    fun `test variable with whitespace around`() = assertArguments("\${OBJECT.run( \${a} , 1 )}", "VAR \${a}", "COMMA", "ARG 1")

    fun `test variable name containing dot is lexed as extended access inside argument`() = assertArguments("\${OBJECT.run(\${other.value})}", "VAR \${other.value}")

    fun `test nested extended variable with method call inside argument`() = assertArguments(
        "\${OBJECT.run(\${other.get(1, 2)}, 3)}", "VAR \${other.get(1, 2)}", "COMMA", "ARG 3")

    fun `test method call on nested variable inside nested call inside argument`() = assertArguments(
        "\${OBJECT.run(\${a.f(\${b.g(1, 2)})}, 3)}", "VAR \${a.f(\${b.g(1, 2)})}", "COMMA", "ARG 3")

    fun `test variable with list item access in argument`() = assertArguments("\${OBJECT.run(\${list}[0], 2)}", "VAR \${list}[0]", "COMMA", "ARG 2")

    fun `test dot in name without method call stays plain attribute chain`() = assertArguments("\${a.b.c.run(1)}", "ARG 1")

    fun `test unclosed method call does not hang and ends at end of line`() {
        val tokens = lexLine("\${OBJECT.run(1, 2")
        assertTrue(tokens.any { it.first == "METHOD_CALL_ARGUMENT" })
    }

    fun `test text following the variable is lexed normally`() {
        val tokens = lexLine("\${OBJECT.run(1)}    \${OTHER}")
        assertEquals("OTHER", tokens.first { it.first == "VARIABLE_BODY" && it.second == "OTHER" }.second)
    }

    fun `test two calls in one line do not share state`() {
        assertArguments("\${A.run('(', 1)}    \${B.run(2)}", "ARG '('", "COMMA", "ARG 1")
        val tokens = lexLine("\${A.run('(', 1)}    \${B.run(2)}")
        val bTokens = tokens.dropWhile { it.second != "B" }
        assertEquals(listOf("B", ".", "run", "(", "2", ")", "}"), bTokens.map { it.second })
    }

    fun `test plain variables are unaffected`() {
        val tokens = lexLine("\${plain} \${with space} @{list} &{dict} %{ENV}")
        assertEquals(
            listOf("plain", "with space", "list", "dict", "ENV"), tokens.filter { it.first == "VARIABLE_BODY" }.map { it.second })
        assertTrue(tokens.none { it.first.startsWith("METHOD_CALL") })
    }
}
