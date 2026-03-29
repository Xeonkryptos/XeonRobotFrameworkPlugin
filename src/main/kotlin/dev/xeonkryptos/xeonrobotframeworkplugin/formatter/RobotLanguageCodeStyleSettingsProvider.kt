package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizableOptions
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage

class RobotLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun getLanguage(): Language = RobotLanguage.INSTANCE

    override fun getIndentOptionsEditor(): IndentOptionsEditor = RobotIndentOptionsEditor()

    override fun customizeDefaults(commonSettings: CommonCodeStyleSettings, indentOptions: CommonCodeStyleSettings.IndentOptions) {
        commonSettings.KEEP_BLANK_LINES_IN_CODE = 1
        commonSettings.ALIGN_MULTILINE_PARAMETERS = false
        commonSettings.KEEP_FIRST_COLUMN_COMMENT = false
        commonSettings.KEEP_LINE_BREAKS = true
    }

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        val customizableOptions = CodeStyleSettingsCustomizableOptions.getInstance()
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> {
                consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS", "SPACE_WITHIN_BRACES", "SPACE_WITHIN_BRACKETS")
                consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", RobotBundle.message("formatter.space.around.assignment.operators"))
                consumer.renameStandardOption("SPACE_WITHIN_BRACKETS", RobotBundle.message("formatter.space.within.variables.array"))
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "KEEP_ADDITIONAL_SPACES_AFTER_VARIABLE_ASSIGNMENTS",
                    RobotBundle.message("formatter.keep.additional.spaces.after.variable.assignments"),
                    customizableOptions.SPACES_OTHER
                )
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "KEEP_ADDITIONAL_SPACES_BETWEEN_TEMPLATE_VALUES",
                    RobotBundle.message("formatter.keep.additional.spaces.between.template.values"),
                    customizableOptions.SPACES_OTHER
                )
            }

            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> {
                consumer.showStandardOptions(
                    "RIGHT_MARGIN",
                    "KEEP_LINE_BREAKS",
                    "WRAP_LONG_LINES",
                    "CALL_PARAMETERS_WRAP",
                    "METHOD_PARAMETERS_WRAP",
                    "ALIGN_MULTILINE_PARAMETERS",
                    "ALIGN_MULTILINE_PARAMETERS_IN_CALLS",
                    "FOR_STATEMENT_WRAP",
                    "KEEP_FIRST_COLUMN_COMMENT"
                )
                consumer.renameStandardOption("METHOD_PARAMETERS_WRAP", RobotBundle.message("formatter.wrap.keyword.definition.arguments"))
                consumer.renameStandardOption("CALL_PARAMETERS_WRAP", RobotBundle.message("formatter.wrap.keyword.call.arguments"))
                consumer.renameStandardOption("FOR_STATEMENT_WRAP", RobotBundle.message("formatter.for.statement.wrapping.expression"))
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "ALIGN_CONTINUATION_WITH_VARIABLE_DEFINITION",
                    RobotBundle.message("formatter.align.continuation.with.variable.definition"),
                    customizableOptions.WRAPPING_METHOD_ARGUMENTS_WRAPPING
                )
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "CALL_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.place.call.arguments.first.parameter.on.newline"),
                    customizableOptions.WRAPPING_METHOD_PARAMETERS
                )
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "METHOD_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.place.call.arguments.first.parameter"),
                    customizableOptions.WRAPPING_METHOD_ARGUMENTS_WRAPPING
                )
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "FOR_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.for.first.argument.on.newline"),
                    customizableOptions.WRAPPING_FOR_STATEMENT
                )
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "WHILE_STATEMENT_WRAP",
                    RobotBundle.message("formatter.while.statement.wrapping.expression"),
                    null,
                    CodeStyleSettingsCustomizableOptions.getInstance().WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES
                )
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "WHILE_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.while.first.argument.on.newline"),
                    RobotBundle.message("formatter.while.statement.wrapping.expression")
                )
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "LOCAL_SETTINGS_WRAP",
                    RobotBundle.message("formatter.local.settings.wrap"),
                    null,
                    CodeStyleSettingsCustomizableOptions.getInstance().WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES
                )
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "LOCAL_SETTINGS_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.local.settings.first.argument.on.newline"),
                    RobotBundle.message("formatter.local.settings.wrap")
                )
            }

            SettingsType.BLANK_LINES_SETTINGS -> {
                consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE")
                consumer.showCustomOption(RobotCodeStyleSettings::class.java, "KEEP_BLANK_LINES_IN_LOCAL_SETTINGS", RobotBundle.message("formatter.blank.lines.in.local.settings"), null)
            }

            else -> {
                // Ignore
            }
        }
    }

    override fun getCodeSample(settingsType: SettingsType): String {
        return """*** Settings ***
Documentation    Example Test Suite
Library          Collections
Resource         keywords.resource

*** Variables ***
${'$'}{VAR}          Value
${'$'}{LONG_VAR}     Another Value

*** Test Cases ***
Example Test
    Log    Hello, World!
    Should Be Equal    ${'$'}{VAR}    Value

Another Test
    [Documentation]    Another example
    Do Stuff    arg1    arg2    arg3    arg4    arg5

Template Test
    [Template]    My Keyword
    arg1    arg2    arg3    arg4    arg5
    val1    val2    val3    val4    val5

*** Keywords ***
My Keyword
    [Arguments]    ${'$'}{arg1}    ${'$'}{arg2}    ${'$'}{arg3}    ${'$'}{arg4}    ${'$'}{arg5}
    Log    ${'$'}{arg1}
    Some Long Keyword Name    ${'$'}{arg1}    ${'$'}{arg2}    ${'$'}{arg3}    ${'$'}{arg4}    ${'$'}{arg5}
    ${'$'}{result}=    Another Keyword    ${'$'}{arg1}    ${'$'}{arg2}    ${'$'}{arg3}
"""
    }
}
