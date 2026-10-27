package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** Verifies the completion of setting names and imports inside of the settings section. */
class SettingsSectionCompletionTest : BasePlatformTestCase() {

    private fun complete(text: String): List<String> {
        myFixture.configureByText("test.robot", text)
        return myFixture.completeBasic()?.map { it.lookupString } ?: emptyList()
    }

    fun `test settings are proposed on empty line`() {
        val proposals = complete("*** Settings ***\n<caret>\n")
        assertContainsElements(proposals, "Library", "Resource", "Suite Setup", "Test Teardown", "Documentation")
    }

    fun `test settings are proposed with prefix`() {
        val proposals = complete("*** Settings ***\nSuite<caret>\n")
        assertContainsElements(proposals, "Suite Setup", "Suite Teardown")
        assertDoesntContain(proposals, "Library")
    }

    fun `test settings are proposed after other setting`() {
        val proposals = complete("*** Settings ***\nLibrary    Collections\n<caret>\n")
        assertContainsElements(proposals, "Resource", "Suite Setup")
    }

    fun `test single match is inserted before following setting`() {
        myFixture.configureByText("test.robot", "*** Settings ***\nLib<caret>\nResource    foo.resource\n")
        assertNull(myFixture.completeBasic())
        myFixture.checkResult("*** Settings ***\nLibrary    \nResource    foo.resource\n")
    }

    fun `test single match is inserted when other section follows`() {
        myFixture.configureByText("test.robot", "*** Settings ***\nRes<caret>\n\n*** Test Cases ***\nT\n    Log    x\n")
        assertNull(myFixture.completeBasic())
        myFixture.checkResult("*** Settings ***\nResource    \n\n*** Test Cases ***\nT\n    Log    x\n")
    }

    fun `test single match is inserted in resource file`() {
        myFixture.configureByText("test.resource", "*** Settings ***\nLib<caret>\n")
        assertNull(myFixture.completeBasic())
        myFixture.checkResult("*** Settings ***\nLibrary    \n")
    }

    fun `test settings are proposed with lowercase prefix`() {
        assertContainsElements(complete("*** Settings ***\nsuite<caret>\n"), "Suite Setup", "Suite Teardown")
    }

    fun `test settings are not proposed in indented position`() {
        assertDoesntContain(complete("*** Settings ***\nLibrary    Collections\n    <caret>\n"), "Suite Setup")
    }
}
