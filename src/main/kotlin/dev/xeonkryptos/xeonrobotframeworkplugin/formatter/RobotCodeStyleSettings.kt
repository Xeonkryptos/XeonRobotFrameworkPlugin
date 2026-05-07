package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.psi.codeStyle.CodeStyleDefaults.DEFAULT_TAB_SIZE
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.WrapConstant
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

@Suppress("PropertyName")
class RobotCodeStyleSettings(container: CodeStyleSettings) : CustomCodeStyleSettings("Robot", container) {
    companion object {
        const val SUPER_SPACE_SIZE = GlobalConstants.SUPER_SPACE.length
    }

    // ################## Indent settings ##################
    @JvmField
    var AFTER_CONTINUATION_INDENT_SIZE: Int = DEFAULT_TAB_SIZE

    // ################## Wrapping and braces settings ##################

    @JvmField
    var KEEP_SIMPLE_KEYWORD_CALLS_IN_ONE_LINE: Boolean = true

    @JvmField
    var KEEP_SIMPLE_GLOBAL_SETTINGS_IN_ONE_LINE: Boolean = true

    @JvmField
    var FOR_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

    @JvmField
    @WrapConstant
    var WHILE_STATEMENT_WRAP: Int = CommonCodeStyleSettings.DO_NOT_WRAP

    @JvmField
    var WHILE_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

    @JvmField
    @WrapConstant
    var GLOBAL_SETTINGS_WRAP: Int = CommonCodeStyleSettings.DO_NOT_WRAP

    @JvmField
    var GLOBAL_SETTINGS_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

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
    @WrapConstant
    var VARIABLE_DEFINITIONS_WRAP: Int = CommonCodeStyleSettings.DO_NOT_WRAP

    @JvmField
    var VARIABLE_DEFINITIONS_FIRST_ARGUMENT_ON_NEW_LINE: Boolean = false

    @JvmField
    var VARIABLE_DEFINITIONS_ALIGN_FIRST_ARGUMENT: Boolean = true

    @JvmField
    var ALIGN_TEMPLATE_ARGUMENTS_WITH_DATA_DRIVEN_NAMES: Boolean = true

    @JvmField
    var ALIGN_TEMPLATE_ARGUMENTS_WITH_EACH_OTHER: Boolean = true

    // ################## Blank lines ##################
    @JvmField
    var BLANK_LINES_BEFORE_GLOBAL_SETUP_TEARDOWN: Int = 1

    @JvmField
    var BLANK_LINES_AFTER_GLOBAL_SETUP_TEARDOWN: Int = 1

    @JvmField
    var BLANK_LINES_BEFORE_GLOBAL_METADATA: Int = 1

    @JvmField
    var BLANK_LINES_AFTER_GLOBAL_METADATA: Int = 1

    @JvmField
    var BLANK_LINES_BEFORE_GLOBAL_SETTING: Int = 1

    @JvmField
    var BLANK_LINES_AFTER_GLOBAL_SETTING: Int = 1

    @JvmField
    var BLANK_LINES_BEFORE_VARIABLE_STATEMENTS: Int = 0

    @JvmField
    var BLANK_LINES_AFTER_VARIABLE_STATEMENTS: Int = 1

    @JvmField
    var BLANK_LINES_AFTER_LOCAL_SETTINGS: Int = 0
}
