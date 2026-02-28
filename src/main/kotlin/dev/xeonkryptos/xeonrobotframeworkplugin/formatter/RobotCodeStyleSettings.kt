package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

@Suppress("PropertyName")
class RobotCodeStyleSettings(container: CodeStyleSettings) : CustomCodeStyleSettings("RobotCodeStyleSettings", container) {
    companion object {
        const val SUPER_SPACE_SIZE = 2
    }

    @JvmField
    var SPACE_AROUND_VARIABLE_BODY: Boolean = false
}
