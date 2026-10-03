package dev.xeonkryptos.xeonrobotframeworkplugin.psi

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyTargetExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCallId

/** Verifies the references of parts in the extended variable syntax: the base variable, attributes, properties and method calls. */
class ExtendedVariableSyntaxReferenceTest : BasePlatformTestCase() {

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

                def greet(self, other: str) -> str: ...

                def get_address(self) -> Address: ...

                @property
                def home(self) -> Address: ...

            def create_person() -> Person: ...
            """.trimIndent()
        )
    }

    private fun configure(vararg bodyLines: String) {
        val body = bodyLines.joinToString("\n") { "    $it" }
        myFixture.configureByText(
            "test.robot", "*** Settings ***\nLibrary    lib.py\n\n*** Test Cases ***\nT\n    \${p} =    Create Person\n$body\n"
        )
    }

    private inline fun <reified T : PsiElement> elements(): List<T> = PsiTreeUtil.findChildrenOfType(myFixture.file, T::class.java).toList()

    private fun idNamed(name: String, occurrence: Int = 0) = elements<RobotVariableExpressionId>().filter { it.text == name }[occurrence]

    private fun methodCallIdNamed(name: String) = elements<RobotVariableExpressionMethodCallId>().first { it.text == name }

    fun `test base variable resolves to variable definition`() {
        configure("Log    \${p.name}")
        val resolved = idNamed("p").reference.resolve()
        assertInstanceOf(resolved, RobotVariableDefinition::class.java)
    }

    fun `test attribute resolves to python attribute`() {
        configure("Log    \${p.name}")
        val resolved = idNamed("name").reference.resolve()
        assertInstanceOf(resolved, PyTargetExpression::class.java)
        assertEquals("name", (resolved as PyTargetExpression).name)
    }

    fun `test method call resolves to python method`() {
        configure("Log    \${p.greet('x')}")
        val resolved = methodCallIdNamed("greet").reference.resolve()
        assertInstanceOf(resolved, PyFunction::class.java)
        assertEquals("Person", (resolved as PyFunction).containingClass?.name)
    }

    fun `test nested attribute resolves through attribute type`() {
        configure("Log    \${p.address.city}")
        val resolved = idNamed("city").reference.resolve()
        assertInstanceOf(resolved, PyTargetExpression::class.java)
        assertEquals("Address", (resolved as PyTargetExpression).containingClass?.name)
    }

    fun `test method call on result of method call`() {
        configure("Log    \${p.get_address().full(1)}")
        val resolved = methodCallIdNamed("full").reference.resolve()
        assertInstanceOf(resolved, PyFunction::class.java)
        assertEquals("Address", (resolved as PyFunction).containingClass?.name)
    }

    fun `test attribute behind property`() {
        configure("Log    \${p.home.city}")
        val resolved = idNamed("city").reference.resolve()
        assertInstanceOf(resolved, PyTargetExpression::class.java)
        assertEquals("Address", (resolved as PyTargetExpression).containingClass?.name)
    }

    fun `test unknown member does not resolve`() {
        configure("Log    \${p.unknown}", "Log    \${p.unknown.other}", "Log    \${p.missing()}")
        assertNull(idNamed("unknown").reference.resolve())
        assertNull(idNamed("other").reference.resolve())
        assertNull(methodCallIdNamed("missing").reference.resolve())
    }

    fun `test attribute is not a method`() {
        configure("Log    \${p.name()}")
        assertNull(methodCallIdNamed("name").reference.resolve())
    }

    fun `test method accessed without call resolves to the method`() {
        configure("Log    \${p.greet}")
        assertInstanceOf(idNamed("greet").reference.resolve(), PyFunction::class.java)
    }

    fun `test member of undefined variable does not resolve`() {
        configure("Log    \${undefined.name}")
        assertNull(idNamed("undefined").reference.resolve())
        assertNull(idNamed("name").reference.resolve())
    }

    fun `test nested variable in chain stops resolving`() {
        configure("Log    \${p.address.\${attr}.city}")
        assertNull(idNamed("city").reference.resolve())
    }

    private fun configureWithUserKeyword(keywordBody: String, callLine: String) = myFixture.configureByText(
        "test.robot",
        "*** Settings ***\nLibrary    lib.py\n\n*** Test Cases ***\nT\n    \${p} =    Get It\n    $callLine\n\n*** Keywords ***\nGet It\n$keywordBody\n"
    )

    fun `test type from user keyword with Return setting`() {
        configureWithUserKeyword("    \${created} =    Create Person\n    [Return]    \${created}", "Log    \${p.name}")
        assertInstanceOf(idNamed("name").reference.resolve(), PyTargetExpression::class.java)
    }

    fun `test type from user keyword with RETURN statement`() {
        configureWithUserKeyword("    \${created} =    Create Person\n    RETURN    \${created}", "Log    \${p.greet('x')}")
        assertInstanceOf(methodCallIdNamed("greet").reference.resolve(), PyFunction::class.java)
    }

    fun `test type from user keyword returning typed argument`() {
        configureWithUserKeyword("    [Arguments]    \${arg: Person}\n    RETURN    \${arg}", "Log    \${p.name}")
        // Person is not resolvable as python class in the data type reference without an sdk, so only make sure nothing breaks
        idNamed("name").reference.resolve()
    }

    fun `test type from user keyword returning other user keyword result`() {
        myFixture.configureByText(
            "test.robot",
            "*** Settings ***\nLibrary    lib.py\n\n*** Test Cases ***\nT\n    \${p} =    Outer\n    Log    \${p.name}\n\n*** Keywords ***\nOuter\n    \${inner} =    Inner\n    RETURN    \${inner}\n\nInner\n    \${created} =    Create Person\n    RETURN    \${created}\n"
        )
        assertInstanceOf(idNamed("name").reference.resolve(), PyTargetExpression::class.java)
    }

    fun `test recursive user keyword does not hang`() {
        myFixture.configureByText(
            "test.robot",
            "*** Test Cases ***\nT\n    \${p} =    Loop\n    Log    \${p.name}\n\n*** Keywords ***\nLoop\n    \${again} =    Loop\n    RETURN    \${again}\n"
        )
        assertNull(idNamed("name").reference.resolve())
    }

    fun `test user keyword returning several values or text gives no type`() {
        configureWithUserKeyword("    \${created} =    Create Person\n    RETURN    \${created}    other", "Log    \${p.name}")
        assertNull(idNamed("name").reference.resolve())
        configureWithUserKeyword("    RETURN    text \${x}", "Log    \${p.name}")
        assertNull(idNamed("name").reference.resolve())
    }

    fun `test reference ranges cover the whole identifier`() {
        configure("Log    \${p.greet('x')} \${p.name}")
        assertEquals(idNamed("p").textLength, idNamed("p").reference.rangeInElement.length)
        assertEquals(methodCallIdNamed("greet").textLength, methodCallIdNamed("greet").reference.rangeInElement.length)
    }

    fun `test manipulators replace identifiers`() {
        configure("Log    \${p.greet('x')} \${p.name}")
        WriteCommandAction.runWriteCommandAction(project) {
            ElementManipulators.handleContentChange(idNamed("name"), "title")
            ElementManipulators.handleContentChange(methodCallIdNamed("greet"), "welcome")
        }
        val text = myFixture.file.text
        assertTrue(text, text.contains("\${p.welcome('x')}"))
        assertTrue(text, text.contains("\${p.title}"))
    }

    fun `test rename of python method updates method call`() {
        configure("Log    \${p.greet('x')}")
        val function = methodCallIdNamed("greet").reference.resolve() as PyFunction
        myFixture.renameElement(function, "welcome")
        assertTrue(myFixture.file.text, myFixture.file.text.contains("\${p.welcome('x')}"))
    }
}
