package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability.UnresolvedExtendedVariablePartInspection

class UnresolvedExtendedVariablePartInspectionTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(UnresolvedExtendedVariablePartInspection::class.java)
        myFixture.addFileToProject(
            "lib.py", """
            class Address:
                city: str = ""

                def full(self, number: int) -> str: ...

            class Person:
                name: str = ""
                address: Address

                def greet(self, other: str) -> str: ...

                def get_address(self) -> Address: ...

            def create_person() -> Person: ...

            def create_untyped():
                return unknown_call()
            """.trimIndent()
        )
    }

    private data class Warning(val text: String, val message: String)

    private fun warnings(vararg lines: String): List<Warning> {
        val body = lines.joinToString("\n") { "    $it" }
        myFixture.configureByText(
            "test.robot", "*** Settings ***\nLibrary    lib.py\n\n*** Test Cases ***\nT\n    \${p} =    Create Person\n    \${u} =    Create Untyped\n$body\n"
        )
        return myFixture.doHighlighting().filter { it.severity == HighlightSeverity.WARNING && it.description != null }
            .map { Warning(myFixture.file.text.substring(it.startOffset, it.endOffset), it.description) }
    }

    fun `test resolvable parts are not reported`() {
        assertEmpty(warnings("Log    \${p.name}", "Log    \${p.greet('x')}", "Log    \${p.address.city}", "Log    \${p.get_address().full(1)}"))
    }

    fun `test unknown attribute is reported`() {
        assertEquals(listOf(Warning("unknown", "Cannot resolve \"unknown\" in type \"Person\"")), warnings("Log    \${p.unknown}"))
    }

    fun `test unknown method is reported`() {
        assertEquals(listOf(Warning("missing", "Cannot resolve \"missing\" in type \"Person\"")), warnings("Log    \${p.missing(1)}"))
    }

    fun `test unknown member of nested type is reported`() {
        assertEquals(listOf(Warning("zip", "Cannot resolve \"zip\" in type \"Address\"")), warnings("Log    \${p.address.zip}"))
    }

    fun `test only the first unresolved part is reported`() {
        assertEquals(listOf("unknown"), warnings("Log    \${p.unknown.other.third()}").map { it.text })
    }

    fun `test unknown variable is reported`() {
        assertEquals(listOf(Warning("undefined", "Cannot resolve variable \"undefined\"")), warnings("Log    \${undefined.name}"))
    }

    fun `test unknown type of variable is reported on the first member`() {
        assertEquals(
            listOf(Warning("name", "Cannot resolve \"name\": the type of the object it is accessed on is unknown")), warnings("Log    \${u.name.upper()}")
        )
    }

    fun `test parts behind a nested variable are not reported`() {
        assertEmpty(warnings("Log    \${p.address.\${attr}.city}"))
    }

    fun `test every expression is checked on its own`() {
        assertEquals(listOf("one", "two"), warnings("Log    \${p.one} \${p.name} \${p.two()}").map { it.text })
    }

    fun `test plain variables are not reported`() {
        assertEmpty(warnings("Log    \${p}", "Log    \${undefined}"))
    }
}
