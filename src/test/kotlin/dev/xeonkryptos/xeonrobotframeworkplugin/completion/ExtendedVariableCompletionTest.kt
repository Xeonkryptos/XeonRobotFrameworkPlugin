package dev.xeonkryptos.xeonrobotframeworkplugin.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** Verifies the completion of members of objects accessed with the extended variable syntax, e.g. `${OBJECT.<caret>}`. */
class ExtendedVariableCompletionTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.addFileToProject(
            "lib.py", """
            class Address:
                city: str = ""

                def full(self, number: int) -> str: ...

            class Person:
                name: str = ""
                address: Address

                def __init__(self) -> None:
                    self.nickname = ""

                def greet(self, other: str) -> str: ...

                def get_address(self) -> Address: ...

                def reset(self) -> None: ...

                @property
                def home(self) -> Address: ...

            def create_person() -> Person: ...
            """.trimIndent()
        )
    }

    private fun robotText(line: String) =
        "*** Settings ***\nLibrary    lib.py\n\n*** Variables ***\n\${greeting}    hello\n\n*** Test Cases ***\nT\n    \${p} =    Create Person\n    $line\n"

    private fun configure(line: String) = myFixture.configureByText("test.robot", robotText(line))

    private fun complete(line: String): List<String> {
        configure(line)
        return myFixture.completeBasic()?.map { it.lookupString } ?: emptyList()
    }

    fun `test members are proposed after the access operator`() {
        assertContainsElements(complete("Log    \${p.<caret>}"), "name", "address", "nickname", "greet", "get_address", "reset", "home")
    }

    fun `test private members are not proposed`() {
        assertDoesntContain(complete("Log    \${p.<caret>}"), "__init__", "__class__")
    }

    fun `test other variables are not proposed in member position`() {
        assertDoesntContain(complete("Log    \${p.<caret>}"), "p", "greeting")
    }

    fun `test members are filtered by prefix`() {
        val proposals = complete("Log    \${p.g<caret>}")
        assertContainsElements(proposals, "greet", "get_address")
        assertDoesntContain(proposals, "name", "reset")
    }

    fun `test members of attribute type`() {
        val proposals = complete("Log    \${p.address.<caret>}")
        assertContainsElements(proposals, "city", "full")
        assertDoesntContain(proposals, "greet")
    }

    fun `test members of method call result`() {
        assertContainsElements(complete("Log    \${p.get_address().<caret>}"), "city", "full")
    }

    fun `test members of property type`() {
        assertContainsElements(complete("Log    \${p.home.<caret>}"), "city", "full")
    }

    fun `test only methods are proposed for an existing method call name`() {
        val proposals = complete("Log    \${p.<caret>greet()}")
        assertContainsElements(proposals, "greet", "get_address", "reset")
        assertDoesntContain(proposals, "name", "address", "nickname", "home")
    }

    fun `test only methods are proposed for the middle of an existing method call name`() {
        val proposals = complete("Log    \${p.g<caret>reet()}")
        assertContainsElements(proposals, "greet", "get_address")
        assertDoesntContain(proposals, "name", "address")
    }

    fun `test no proposals for unknown variable`() {
        assertDoesntContain(complete("Log    \${undefined.<caret>}"), "name", "greet")
    }

    fun `test no member proposals behind a nested variable`() {
        assertDoesntContain(complete("Log    \${p.\${attr}.<caret>}"), "city", "full")
    }

    fun `test attribute is inserted without parentheses`() {
        configure("Log    \${p.nickn<caret>}")
        myFixture.completeBasic()
        myFixture.checkResult(robotText("Log    \${p.nickname<caret>}"))
    }

    fun `test method with parameters is inserted with parentheses and caret inside`() {
        configure("Log    \${p.gree<caret>}")
        myFixture.completeBasic()
        myFixture.checkResult(robotText("Log    \${p.greet(<caret>)}"))
    }

    fun `test method without parameters is inserted with parentheses and caret behind`() {
        configure("Log    \${p.rese<caret>}")
        myFixture.completeBasic()
        myFixture.checkResult(robotText("Log    \${p.reset()<caret>}"))
    }

    fun `test members of type returned by user keyword`() {
        myFixture.configureByText(
            "test.robot",
            "*** Settings ***\nLibrary    lib.py\n\n*** Test Cases ***\nT\n    \${p} =    Get It\n    Log    \${p.<caret>}\n\n*** Keywords ***\nGet It\n    \${created} =    Create Person\n    [Return]    \${created}\n"
        )
        val proposals = myFixture.completeBasic()?.map { it.lookupString } ?: emptyList()
        assertContainsElements(proposals, "name", "greet", "home")
    }

    fun `test existing parentheses are not duplicated`() {
        configure("Log    \${p.rese<caret>()}")
        myFixture.completeBasic()
        myFixture.checkResult(robotText("Log    \${p.reset<caret>()}"))
    }

    fun `test extended variable proposals are marked with the highest weighted context`() {
        configure("Log    \${p.<caret>}")
        myFixture.completeBasic()
        val items = myFixture.lookupElements!!
        assertTrue(items.all { it.getUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT) == RobotLookupContext.EXTENDED_VARIABLE_SYNTAX })
    }
}
