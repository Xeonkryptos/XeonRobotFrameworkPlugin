package dev.xeonkryptos.xeonrobotframeworkplugin.psi

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionArrayAccess
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionDefinition

/** Verifies the PSI structure of array accesses in the extended variable syntax, e.g. `${Partner.bankverbindung[0].iban}`. */
class ExtendedVariableSyntaxArrayAccessParsingTest : BasePlatformTestCase() {

    private fun accesses(expression: String): List<RobotVariableExpressionArrayAccess> {
        val file = myFixture.configureByText("test.robot", "*** Test Cases ***\nT\n    Log    $expression\n")
        val errors = PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java)
        assertEmpty("Unexpected parse errors ${errors.map { it.errorDescription }} in $expression", errors)
        val definition = PsiTreeUtil.findChildOfType(file, RobotVariableExpressionDefinition::class.java)!!
        return definition.variableExpressionArrayAccessList
    }

    fun `test index access`() {
        val access = accesses("\${Partner.bankverbindung[0].iban}").single()
        assertEquals("[0]", access.text)
        assertNotNull(access.variableIndexAccessContent)
    }

    fun `test index access behind method call`() {
        val access = accesses("\${Partner.getBankverbindungen()[0].iban}").single()
        assertEquals("[0]", access.text)
        assertNotNull(access.variableIndexAccessContent)
    }

    fun `test slice access`() = assertNotNull(accesses("\${a.b[1:2].c}").single().variableSliceAccessContent)

    fun `test key and variable access`() {
        assertNotNull(accesses("\${a.b['key'].c}").single().variableNestedAccessContent)
        assertNotNull(accesses("\${a.b[\${i}].c}").single().variableNestedAccessContent)
    }

    fun `test multiple and chained accesses`() {
        assertEquals(listOf("[0]", "[1]"), accesses("\${a.b[0][1].c}").map { it.text })
        assertEquals(listOf("[0]", "[1]"), accesses("\${a.b[0].c[1]}").map { it.text })
    }

    fun `test trailing access operator after array access is allowed`() {
        assertEquals("[0]", accesses("\${Partner.bankverbindungen[0].}").single().text)
        assertEquals("[0]", accesses("\${Partner.getBankverbindungen()[0].}").single().text)
    }

    fun `test trailing access operator without member is allowed`() {
        val file = myFixture.configureByText("test.robot", "*** Test Cases ***\nT\n    Log    \${Partner.}\n")
        assertEmpty(PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java))
        assertNotNull(PsiTreeUtil.findChildOfType(file, RobotVariableExpressionDefinition::class.java))
    }

    fun `test in variable definition`() {
        val file = myFixture.configureByText("test.robot", "*** Variables ***\n\${x}    \${a.b[0].c}\n")
        assertEmpty(PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java))
    }
}
