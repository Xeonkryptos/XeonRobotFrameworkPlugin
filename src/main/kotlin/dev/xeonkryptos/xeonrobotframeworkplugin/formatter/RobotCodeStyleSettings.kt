package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.psi.codeStyle.CodeStyleDefaults.DEFAULT_TAB_SIZE
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.WrapConstant
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
    var ALIGN_CONTINUATION_WITH_VARIABLE_DEFINITION: Boolean = false

    @JvmField
    var FOR_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

    @JvmField
    @WrapConstant
    var WHILE_STATEMENT_WRAP: Int = CommonCodeStyleSettings.DO_NOT_WRAP

    @JvmField
    var WHILE_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

    @JvmField
    @WrapConstant
    var LOCAL_SETTINGS_WRAP: Int = CommonCodeStyleSettings.DO_NOT_WRAP

    @JvmField
    var LOCAL_SETTINGS_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

    @JvmField
    var CALL_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

    @JvmField
    var METHOD_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

    @JvmField
    var KEEP_BLANK_LINES_IN_LOCAL_SETTINGS: Int = 1
}
