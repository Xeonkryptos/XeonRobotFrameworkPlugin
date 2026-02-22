package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

class RobotCodeStyleSettings(container: CodeStyleSettings) : CustomCodeStyleSettings("RobotCodeStyleSettings", container) {
    @JvmField var ALIGN_SETTINGS = true
    @JvmField var ALIGN_VARIABLES = true
    @JvmField var ALIGN_TEST_CASES = true
    @JvmField var ALIGN_KEYWORDS = true
}
