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
        commonSettings.KEEP_FIRST_COLUMN_COMMENT = false
        commonSettings.KEEP_LINE_BREAKS = true
        commonSettings.KEEP_BLANK_LINES_IN_CODE = 1
    }

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        val customizableOptions = CodeStyleSettingsCustomizableOptions.getInstance()
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> {
                consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS", "SPACE_WITHIN_BRACES", "SPACE_WITHIN_BRACKETS")
                consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", RobotBundle.message("formatter.space.around.assignment.operators"))
                consumer.renameStandardOption("SPACE_WITHIN_BRACKETS", RobotBundle.message("formatter.space.within.variables.array"))
            }

            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> {
                consumer.showStandardOptions("RIGHT_MARGIN", "KEEP_LINE_BREAKS", "WRAP_LONG_LINES", "CALL_PARAMETERS_WRAP", "METHOD_PARAMETERS_WRAP", "FOR_STATEMENT_WRAP")
                consumer.renameStandardOption("METHOD_PARAMETERS_WRAP", RobotBundle.message("formatter.wrap.keyword.definition.arguments"))
                consumer.renameStandardOption("CALL_PARAMETERS_WRAP", RobotBundle.message("formatter.wrap.keyword.call.arguments"))
                consumer.renameStandardOption("FOR_STATEMENT_WRAP", RobotBundle.message("formatter.for.statement.wrapping.expression"))

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                                          "KEEP_SIMPLE_KEYWORD_CALLS_IN_ONE_LINE",
                                          RobotBundle.message("formatter.keep.simple.keyword.calls.in.one.line"),
                                          customizableOptions.WRAPPING_KEEP)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                                          "KEEP_SIMPLE_GLOBAL_SETTINGS_IN_ONE_LINE",
                                          RobotBundle.message("formatter.keep.simple.global.settings.in.one.line"),
                                          customizableOptions.WRAPPING_KEEP)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                                          "KEEP_SIMPLE_LOCAL_SETTINGS_IN_ONE_LINE",
                                          RobotBundle.message("formatter.keep.simple.local.settings.in.one.line"),
                                          customizableOptions.WRAPPING_KEEP)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                                          "KEEP_SIMPLE_VARIABLE_STATEMENT_IN_ONE_LINE",
                                          RobotBundle.message("formatter.keep.simple.variable.statement.in.one.line"),
                                          customizableOptions.WRAPPING_KEEP)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "CALL_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.place.call.arguments.first.parameter.on.newline"),
                    customizableOptions.WRAPPING_METHOD_ARGUMENTS_WRAPPING)

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "METHOD_PARAMETERS_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.place.call.arguments.first.parameter"),
                    customizableOptions.WRAPPING_METHOD_PARAMETERS)

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "GLOBAL_SETTINGS_WRAP",
                    RobotBundle.message("formatter.global.settings.wrap"),
                    null,
                    CodeStyleSettingsCustomizableOptions.getInstance().WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "GLOBAL_SETTINGS_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.global.settings.first.argument.on.newline"),
                    RobotBundle.message("formatter.global.settings.wrap"))

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "FOR_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.for.first.argument.on.newline"),
                    customizableOptions.WRAPPING_FOR_STATEMENT)

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "WHILE_STATEMENT_WRAP",
                    RobotBundle.message("formatter.while.statement.wrapping.expression"),
                    null,
                    CodeStyleSettingsCustomizableOptions.getInstance().WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "WHILE_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.while.first.argument.on.newline"),
                    RobotBundle.message("formatter.while.statement.wrapping.expression"))

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "LOCAL_SETTINGS_WRAP",
                    RobotBundle.message("formatter.local.settings.wrap"),
                    null,
                    CodeStyleSettingsCustomizableOptions.getInstance().WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "LOCAL_SETTINGS_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.local.settings.first.argument.on.newline"),
                    RobotBundle.message("formatter.local.settings.wrap"))

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "VARIABLE_DEFINITIONS_WRAP",
                    RobotBundle.message("formatter.variable.definitions.wrap"),
                    null,
                    CodeStyleSettingsCustomizableOptions.getInstance().WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "VARIABLE_DEFINITIONS_FIRST_ARGUMENT_ON_NEW_LINE",
                    RobotBundle.message("formatter.variable.definitions.first.argument.on.newline"),
                    RobotBundle.message("formatter.variable.definitions.wrap"))
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "VARIABLE_DEFINITIONS_ALIGN_FIRST_ARGUMENT",
                    RobotBundle.message("formatter.variable.definitions.align.first.arguments"),
                    RobotBundle.message("formatter.variable.definitions.wrap"))

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "ALIGN_TEMPLATE_ARGUMENTS_WITH_DATA_DRIVEN_NAMES",
                    RobotBundle.message("formatter.template.arguments.align.with.data.driven.names"),
                    RobotBundle.message("formatter.template.arguments"))
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "ALIGN_TEMPLATE_ARGUMENTS_WITH_EACH_OTHER",
                    RobotBundle.message("formatter.template.arguments.align.with.each.other"),
                    RobotBundle.message("formatter.template.arguments"))
            }

            SettingsType.BLANK_LINES_SETTINGS -> {
                consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE",
                    "BLANK_LINES_BEFORE_IMPORTS",
                    "BLANK_LINES_AFTER_IMPORTS",
                    "BLANK_LINES_AFTER_CLASS_HEADER")

                consumer.renameStandardOption("BLANK_LINES_AFTER_CLASS_HEADER", RobotBundle.message("formatter.blank.lines.after.testcase.task.name"))
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_BEFORE_GLOBAL_SETUP_TEARDOWN",
                    RobotBundle.message("formatter.blank.lines.before.global.setup.teardown"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_AFTER_GLOBAL_SETUP_TEARDOWN",
                    RobotBundle.message("formatter.blank.lines.after.global.setup.teardown"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_BEFORE_GLOBAL_METADATA",
                    RobotBundle.message("formatter.blank.lines.before.global.metadata"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_AFTER_GLOBAL_METADATA",
                    RobotBundle.message("formatter.blank.lines.after.global.metadata"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_BEFORE_GLOBAL_SETTING",
                    RobotBundle.message("formatter.blank.lines.before.global.setting"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_AFTER_GLOBAL_SETTING",
                    RobotBundle.message("formatter.blank.lines.after.global.setting"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_BEFORE_VARIABLE_STATEMENTS",
                    RobotBundle.message("formatter.blank.lines.before.variable.statements"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_AFTER_VARIABLE_STATEMENTS",
                    RobotBundle.message("formatter.blank.lines.after.variable.statements"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "BLANK_LINES_AFTER_LOCAL_SETTINGS",
                    RobotBundle.message("formatter.blank.lines.after.local.settings"),
                    CodeStyleSettingsCustomizableOptions.getInstance().BLANK_LINES)
            }

            else -> { // Ignore
            }
        }
    }

    override fun getCodeSample(settingsType: SettingsType): String = when (settingsType) {
        SettingsType.INDENT_SETTINGS -> FormatterPreviews.INDENT
        SettingsType.SPACING_SETTINGS -> FormatterPreviews.SPACING
        SettingsType.WRAPPING_AND_BRACES_SETTINGS -> FormatterPreviews.WRAPPING_AND_BRACES
        SettingsType.BLANK_LINES_SETTINGS -> FormatterPreviews.BLANK_LINES
        else -> FormatterPreviews.WRAPPING_AND_BRACES
    }
}
