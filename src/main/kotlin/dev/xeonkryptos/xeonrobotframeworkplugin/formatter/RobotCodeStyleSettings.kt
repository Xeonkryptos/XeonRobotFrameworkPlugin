package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.psi.codeStyle.CodeStyleDefaults.DEFAULT_TAB_SIZE
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

@Suppress("PropertyName")
class RobotCodeStyleSettings(container: CodeStyleSettings) : CustomCodeStyleSettings("Robot", container) {
    companion object {
        const val SUPER_SPACE_SIZE = 2
    }

    // ################## Indent settings ##################
    @JvmField
    var AFTER_CONTINUATION_INDENT_SIZE: Int = DEFAULT_TAB_SIZE

    // ################## Spacing settings ##################
    @JvmField
    var KEEP_ADDITIONAL_SPACES_BETWEEN_TEMPLATE_VALUES: Boolean = true
    @JvmField
    var KEEP_ADDITIONAL_SPACES_AFTER_VARIABLE_ASSIGNMENTS: Boolean = true

    // ################## Wrapping and braces settings ##################
    @JvmField
    var ALIGN_CONTINUATION_WITH_VARIABLE_DEFINITION: Boolean = true
}
