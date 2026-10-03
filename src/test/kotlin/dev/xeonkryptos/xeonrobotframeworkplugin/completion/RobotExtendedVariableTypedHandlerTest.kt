package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate.Result
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RobotExtendedVariableTypedHandlerTest : BasePlatformTestCase() {

    private fun resultFor(typedChar: Char, line: String): Result {
        myFixture.configureByText("test.robot", "*** Test Cases ***\nT\n    $line\n")
        return RobotExtendedVariableTypedHandler().checkAutoPopup(typedChar, project, myFixture.editor, myFixture.file)
    }

    fun `test popup after access operator within variable`() = assertEquals(Result.STOP, resultFor('.', "Log    \${obj<caret>}"))

    fun `test popup after access operator behind another variable within variable`() = assertEquals(Result.STOP, resultFor('.', "Log    \${a} \${obj<caret>}"))

    fun `test popup after access operator in nested variable`() = assertEquals(Result.STOP, resultFor('.', "Log    \${a.\${obj<caret>}}"))

    fun `test popup after access operator within list variable`() = assertEquals(Result.STOP, resultFor('.', "Log    @{obj<caret>}"))

    fun `test no popup outside of variables`() {
        assertEquals(Result.CONTINUE, resultFor('.', "Log    text<caret>"))
        assertEquals(Result.CONTINUE, resultFor('.', "Log    \${obj}<caret>"))
        assertEquals(Result.CONTINUE, resultFor('.', "Log    \${a} text<caret>"))
    }

    fun `test no popup for other characters`() = assertEquals(Result.CONTINUE, resultFor('a', "Log    \${obj<caret>}"))
}
