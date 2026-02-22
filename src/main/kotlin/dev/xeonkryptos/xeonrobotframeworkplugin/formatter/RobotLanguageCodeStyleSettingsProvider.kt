package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage

class RobotLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun getLanguage(): Language = RobotLanguage.INSTANCE

    override fun getIndentOptionsEditor(): IndentOptionsEditor = SmartIndentOptionsEditor()

    override fun customizeDefaults(commonSettings: CommonCodeStyleSettings, indentOptions: CommonCodeStyleSettings.IndentOptions) {
        indentOptions.INDENT_SIZE = 4
    }

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS")
            consumer.showStandardOptions("SPACE_BEFORE_COMMA")
            consumer.showStandardOptions("SPACE_AFTER_COMMA")
        } else if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showCustomOption(RobotCodeStyleSettings::class.java, "ALIGN_SETTINGS", "Align Settings", "Alignment")
            consumer.showCustomOption(RobotCodeStyleSettings::class.java, "ALIGN_VARIABLES", "Align Variables", "Alignment")
            consumer.showCustomOption(RobotCodeStyleSettings::class.java, "ALIGN_TEST_CASES", "Align Test Cases", "Alignment")
            consumer.showCustomOption(RobotCodeStyleSettings::class.java, "ALIGN_KEYWORDS", "Align Keywords", "Alignment")
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
