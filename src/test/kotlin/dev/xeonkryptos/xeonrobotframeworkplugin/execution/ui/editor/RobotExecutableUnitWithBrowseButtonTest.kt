package dev.xeonkryptos.xeonrobotframeworkplugin.execution.ui.editor

import dev.xeonkryptos.xeonrobotframeworkplugin.execution.ui.editor.RobotExecutableUnitWithBrowseButton.Companion.extractLeafName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RobotExecutableUnitWithBrowseButtonTest {

    @Test
    fun `when qualified test case name consists of an escaped dot then correctly extract the complete test case name without the dot`() {
        val result = extractLeafName("My.Testcases.In.A.Subdirectory.My Really Fancy Testcase\\. It contains a Dot")
        assertEquals("My Really Fancy Testcase. It contains a Dot", result)
    }

    @Test
    fun `when qualified test case name doesn't consist of an escaped dot then correctly extract the complete test case name`() {
        val result = extractLeafName("My.Testcases.In.A.Subdirectory.My Really Fancy Testcase")
        assertEquals("My Really Fancy Testcase", result)
    }

    @Test
    fun `when extracting location from qualified name with escaped dot then correctly extract location`() {
        val result =
            RobotExecutableUnitWithBrowseButton.extractLocation(
                "My.Testcases.In.A.Subdirectory.My Really Fancy Testcase\\. It contains a Dot",
                "My Really Fancy Testcase. It contains a Dot"
            )
        assertEquals("My.Testcases.In.A.Subdirectory", result)
    }

    @Test
    fun `when extracting location from qualified name without escaped dot then correctly extract location`() {
        val result =
            RobotExecutableUnitWithBrowseButton.extractLocation(
                "My.Testcases.In.A.Subdirectory.My Really Fancy Testcase",
                "My Really Fancy Testcase"
            )
        assertEquals("My.Testcases.In.A.Subdirectory", result)
    }
}
