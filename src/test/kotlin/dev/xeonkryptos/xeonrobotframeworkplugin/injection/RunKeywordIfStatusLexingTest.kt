package dev.xeonkryptos.xeonrobotframeworkplugin.injection

import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent

/**
 * Regression test for the false "':' expected" error on `Run Keyword If Test Failed    <Keyword>`. That keyword (and
 * `Run Keyword If Test Passed`/`All Tests Passed`/`Any Tests Failed`/`Timeout Occurred`) takes a KEYWORD to run, not a
 * Python condition. The lexer used to treat the argument as a condition, so the plugin injected `if <keyword>:` and the
 * Python parser complained. Only bare `Run Keyword If`/`Unless`/`And Return If` take a real condition.
 */
class RunKeywordIfStatusLexingTest : BasePlatformTestCase() {

    private fun conditionalContentsIn(robotText: String): List<RobotConditionalContent> {
        myFixture.configureByText("test.robot", robotText)
        return PsiTreeUtil.findChildrenOfType(myFixture.file, RobotConditionalContent::class.java).toList()
    }

    fun `test Run Keyword If Test Failed argument is a keyword, not a condition`() {
        val conditions = conditionalContentsIn(
            """
            *** Test Cases ***
            T
                [Teardown]    Run Keyword If Test Failed    Log    boom
            """.trimIndent()
        )
        assertEmpty("Run Keyword If Test Failed takes a keyword, not a Python condition: ${conditions.map { it.text }}", conditions)
    }

    fun `test bare Run Keyword If still parses its first argument as a condition`() {
        // Control: the genuinely condition-taking form must keep injecting/parsing its condition.
        val conditions = conditionalContentsIn(
            """
            *** Test Cases ***
            T
                Run Keyword If    ${'$'}{x} == 1    Log    hi
            """.trimIndent()
        )
        assertNotEmpty(conditions)
    }
}