package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.cleanup.RobotUnusedVariableDefinitionInspection

class RobotUnusedVariableDefinitionInspectionTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(RobotUnusedVariableDefinitionInspection::class.java)
    }

    private fun unusedVariables(vararg lines: String): List<String> {
        val body = lines.joinToString("\n") { "    $it" }
        myFixture.configureByText("test.robot", "*** Test Cases ***\nT\n$body\n")
        return myFixture.doHighlighting().filter { it.description == "Unused variable definition" }
            .map { myFixture.file.text.substring(it.startOffset, it.endOffset) }
    }

    fun `test variable with dot in name used as named argument value is used`() {
        assertEmpty(
            unusedVariables(
                "\${Partner.bpid}=  Set Variable  Dummy",
                "",
                "Some Keyword",
                "...    bpId=\${Partner.bpid}",
                "...    number=\${AnNr}"
            )
        )
    }

    fun `test variable with dot in name used as plain argument is used`() {
        assertEmpty(unusedVariables("\${Partner.bpid}=  Set Variable  Dummy", "Log    \${Partner.bpid}"))
    }

    fun `test variable with dot in name without usage is reported`() {
        assertEquals(listOf("\${Partner.bpid}"), unusedVariables("\${Partner.bpid}=  Set Variable  Dummy", "Log    other"))
    }
}
