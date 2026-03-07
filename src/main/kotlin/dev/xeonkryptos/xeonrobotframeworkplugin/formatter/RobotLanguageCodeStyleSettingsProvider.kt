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
                // TODO: Re-enable options related to wrapping when a solution is available for the missing continuation marker on wrapped lines
                consumer.showStandardOptions("RIGHT_MARGIN", "KEEP_LINE_BREAKS"/*, "WRAP_LONG_LINES", "WRAP_ON_TYPING", "CALL_PARAMETERS_WRAP", "METHOD_PARAMETERS_WRAP"*/)
                /*consumer.renameStandardOption("METHOD_PARAMETERS_WRAP", RobotBundle.message("formatter.wrap.keyword.definition.arguments"))
                consumer.renameStandardOption("CALL_PARAMETERS_WRAP", RobotBundle.message("formatter.wrap.keyword.call.arguments"))*/
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "ALIGN_CONTINUATION_WITH_VARIABLE_DEFINITION",
                    RobotBundle.message("formatter.align.continuation.with.variable.definition"),
                    customizableOptions.WRAPPING_METHOD_ARGUMENTS_WRAPPING
                )
            }

            SettingsType.BLANK_LINES_SETTINGS -> {
                consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE")
            }

            else -> {}
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
    Do Stuff    arg1    arg2
"""
    }
}
