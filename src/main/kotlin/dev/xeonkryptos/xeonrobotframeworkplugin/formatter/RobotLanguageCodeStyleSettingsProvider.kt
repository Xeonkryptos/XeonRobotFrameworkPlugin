package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizableOptions
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage

class RobotLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun getLanguage(): Language = RobotLanguage.INSTANCE

    override fun getIndentOptionsEditor(): IndentOptionsEditor = SmartIndentOptionsEditor()

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> {
                consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS")
                consumer.showCustomOption(
                    RobotCodeStyleSettings::class.java,
                    "SPACE_AROUND_VARIABLE_BODY",
                    RobotBundle.message("formatter.space.around.variable.body"),
                    CodeStyleSettingsCustomizableOptions.getInstance().SPACES_AROUND_OPERATORS
                )
            }

            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> {
                consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE", "CALL_PARAMETERS_WRAP", "METHOD_PARAMETERS_WRAP")
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
