package dev.xeonkryptos.xeonrobotframeworkplugin.psi

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCallArgument
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCallId

/**
 * Verifies that robot code using method calls in the extended variable syntax (`${OBJECT.doSomething(1234, ${var})}`) is parsed without errors and
 * into the expected PSI structure, in all places a variable can be used.
 */
class ExtendedVariableSyntaxMethodCallParsingTest : BasePlatformTestCase() {

    private fun parse(robotText: String) = myFixture.configureByText("test.robot", robotText.trimIndent() + "\n")

    private fun assertNoErrors(robotText: String) {
        val file = parse(robotText)
        val errors = PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java)
        assertEmpty("Unexpected parse errors ${errors.map { it.errorDescription + " at " + it.textOffset }} in:\n$robotText", errors)
    }

    private fun methodCalls(robotText: String): List<RobotVariableExpressionMethodCall> {
        assertNoErrors(robotText)
        return PsiTreeUtil.findChildrenOfType(myFixture.file, RobotVariableExpressionMethodCall::class.java).toList()
    }

    private fun argumentTexts(call: RobotVariableExpressionMethodCall): List<String> =
        PsiTreeUtil.getChildrenOfTypeAsList(call, RobotVariableExpressionMethodCallArgument::class.java).map { it.text }

    private fun keywordCall(expression: String) = """
        *** Test Cases ***
        T
            Log    $expression
    """

    private fun assertArguments(expression: String, vararg expected: String) {
        val calls = methodCalls(keywordCall(expression))
        assertEquals("Exactly one method call expected in $expression", 1, calls.size)
        assertEquals(expression, expected.toList(), argumentTexts(calls.single()))
    }

    fun `test method call without arguments`() = assertArguments("\${OBJECT.run()}")

    fun `test method call name is parsed as id`() {
        val call = methodCalls(keywordCall("\${OBJECT.doSomething(1234)}")).single()
        assertEquals("doSomething", PsiTreeUtil.getChildOfType(call, RobotVariableExpressionMethodCallId::class.java)!!.text)
    }

    fun `test single numeric argument`() = assertArguments("\${OBJECT.doSomething(1234)}", "1234")

    fun `test multiple arguments`() = assertArguments("\${OBJECT.run(1, 'two', [3, 4])}", "1", "'two'", "[3, 4]")

    fun `test arguments with separators in strings and brackets`() = assertArguments(
        "\${OBJECT.run('a,b)', foo(1, 2), {'k': [1, 2]})}", "'a,b)'", "foo(1, 2)", "{'k': [1, 2]}")

    fun `test variable as argument`() = assertArguments("\${OBJECT.doSomething(\${anotherVar})}", "\${anotherVar}")

    fun `test variable with prefix and suffix`() = assertArguments("\${OBJECT.run(pre\${x}suf, \${a}b\${c}d)}", "pre\${x}suf", "\${a}b\${c}d")

    fun `test variable inside string and brackets`() = assertArguments(
        "\${OBJECT.run('a,\${x},b)', [\${a}, 1], foo(\${b}, 2))}", "'a,\${x},b)'", "[\${a}, 1]", "foo(\${b}, 2)")

    fun `test all variable kinds as arguments`() = assertArguments("\${OBJECT.run(\${a}, @{b}, &{c}, %{D})}", "\${a}", "@{b}", "&{c}", "%{D}")

    fun `test sigils as plain text`() = assertArguments("\${OBJECT.run(50%, a % b, '\$', @)}", "50%", "a % b", "'$'", "@")

    fun `test variables in arguments are real variable elements`() {
        val call = methodCalls(keywordCall("\${OBJECT.run(pre\${x}suf, \${y})}")).single()
        val variables = PsiTreeUtil.findChildrenOfType(call, RobotVariable::class.java).map { it.text }
        assertEquals(listOf("\${x}", "\${y}"), variables)
    }

    fun `test chained method calls`() {
        val calls = methodCalls(keywordCall("\${OBJECT.first(1).second(2, 3).third()}"))
        assertEquals(listOf("first", "second", "third"), calls.map { PsiTreeUtil.getChildOfType(it, RobotVariableExpressionMethodCallId::class.java)!!.text })
        assertEquals(listOf(listOf("1"), listOf("2", "3"), emptyList()), calls.map { argumentTexts(it) })
    }

    fun `test attribute access mixed with method calls`() {
        val calls = methodCalls(keywordCall("\${OBJECT.attr.run(1).other}"))
        assertEquals(1, calls.size)
        assertEquals(listOf("1"), argumentTexts(calls.single()))
    }

    fun `test attribute access without method call`() = assertNoErrors(keywordCall("\${OBJECT.name} \${OBJECT.nested.name}"))

    fun `test nested extended variable in argument`() {
        val calls = methodCalls(keywordCall("\${OBJECT.run(\${other.get(1, 2)}, 3)}"))
        assertEquals(2, calls.size)
        val outer = calls.first { argumentTexts(it).size == 2 }
        assertEquals(listOf("\${other.get(1, 2)}", "3"), argumentTexts(outer))
    }

    fun `test deeply nested extended variables`() {
        val calls = methodCalls(keywordCall("\${A.f(\${B.g(\${C.h(1)})})}"))
        assertEquals(3, calls.size)
    }

    fun `test variables as part of the access chain`() = assertNoErrors(keywordCall("\${OBJECT.\${attr}.name} \${OBJECT.attr.\${other}} \${OBJECT.first(1).\${attr}.run(2)}"))

    fun `test multiple expressions in one line`() {
        val calls = methodCalls(keywordCall("\${A.run('(', 1)}    \${B.run(2)}"))
        assertEquals(listOf(listOf("'('", "1"), listOf("2")), calls.map { argumentTexts(it) })
    }

    fun `test keyword with named argument and extended variable`() = assertNoErrors(
        """
        *** Test Cases ***
        T
            Log    message=${'$'}{OBJECT.run(1, ${'$'}{x})}    level=INFO
        """)

    fun `test variable assignment from extended variable`() = assertNoErrors(
        """
        *** Test Cases ***
        T
            ${'$'}{result} =    Set Variable    ${'$'}{OBJECT.run(1, ${'$'}{x})}
        """)

    fun `test extended variable in variable section`() = assertNoErrors(
        """
        *** Variables ***
        ${'$'}{VALUE}    ${'$'}{OBJECT.run(1, 'a')}
        """)

    fun `test extended variable in user keyword`() = assertNoErrors(
        """
        *** Keywords ***
        My Keyword
            [Arguments]    ${'$'}{arg}
            Log    ${'$'}{arg.run(1, 'a')}
            [Return]    ${'$'}{arg.run(2)}
        """)

    fun `test extended variable in for loop and if`() = assertNoErrors(
        """
        *** Test Cases ***
        T
            FOR    ${'$'}{item}    IN    @{OBJECT.items(1)}
                Log    ${'$'}{item.name}
            END
            IF    ${'$'}{OBJECT.check(1, 'a')}
                Log    yes
            END
        """)

    fun `test extended variable in settings`() = assertNoErrors(
        """
        *** Settings ***
        Suite Setup    Log    ${'$'}{OBJECT.run(1)}
        """)

    fun `test method call variants stay error free`() {
        val expressions = listOf(
            "\${O.m()}", "\${O.m( )}", "\${O.m(1)}", "\${O.m(1,2)}", "\${O.m( 1 , 2 )}", "\${O.m('a', \"b\")}", "\${O.m(k=1, j=\${v})}", "\${O.m(\${v})}",
            "\${O.m(\${v}\${w})}", "\${O.m(a\${v})}", "\${O.m(\${v}a)}", "\${O.m([\${v}])}", "\${O.m('\${v}')}", "\${O.m(f(\${v}))}", "\${O.m(1).n(2)}",
            "\${O.m(50%)}", "\${O.m(\\\${v})}", "@{O.m(1)}", "&{O.m(1)}"
        )
        expressions.forEach { assertNoErrors(keywordCall(it)) }
    }
}
