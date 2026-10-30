package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.ElementManipulators
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.xeonkryptos.xeonrobotframeworkplugin.formatter.RobotCodeStyleSettings
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName

/** Verifies that [RobotKeywordCallNameElementManipulator] takes over the entered keyword name and only applies capitalization when configured. */
class RobotKeywordCallNameElementManipulatorTest : BasePlatformTestCase() {

    private fun changeKeywordName(newName: String, capitalizeKeywords: Boolean): String {
        val file = myFixture.configureByText("test.robot", "*** Test Cases ***\nTest\n    Old Name\n")
        CodeStyle.getCustomSettings(file, RobotCodeStyleSettings::class.java).CAPITALIZE_KEYWORDS = capitalizeKeywords

        val keywordCallName = PsiTreeUtil.findChildOfType(file, RobotKeywordCallName::class.java)!!
        WriteCommandAction.runWriteCommandAction(project) { ElementManipulators.handleContentChange(keywordCallName, newName) }

        return PsiTreeUtil.findChildOfType(myFixture.file, RobotKeywordCallName::class.java)!!.text
    }

    fun `test underscores are kept when capitalization is enabled`() {
        assertEquals("S4H_BP Partner Anlegen", changeKeywordName("S4H_BP Partner Anlegen", capitalizeKeywords = true))
    }

    fun `test underscores are kept when capitalization is disabled`() {
        assertEquals("S4H_BP partner anlegen", changeKeywordName("S4H_BP partner anlegen", capitalizeKeywords = false))
    }

    fun `test name without underscores is capitalized when enabled`() {
        assertEquals("My New Keyword", changeKeywordName("my new keyword", capitalizeKeywords = true))
    }

    fun `test name without underscores is unchanged when capitalization is disabled`() {
        assertEquals("my new keyword", changeKeywordName("my new keyword", capitalizeKeywords = false))
    }

    fun `test already capitalized name is unchanged when enabled`() {
        assertEquals("Already Capitalized", changeKeywordName("Already Capitalized", capitalizeKeywords = true))
    }

    fun `test inner capital letters are preserved when capitalization is enabled`() {
        assertEquals("Open SAP Gui", changeKeywordName("open SAP Gui", capitalizeKeywords = true))
    }

    fun `test single word is capitalized when enabled`() {
        assertEquals("Login", changeKeywordName("login", capitalizeKeywords = true))
    }

    fun `test name with digits is kept`() {
        assertEquals("Step 2 Check", changeKeywordName("step 2 check", capitalizeKeywords = true))
    }
}
