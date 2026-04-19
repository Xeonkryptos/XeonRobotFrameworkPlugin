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
    }

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        val customizableOptions = CodeStyleSettingsCustomizableOptions.getInstance()
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> {
                consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS", "SPACE_WITHIN_BRACES", "SPACE_WITHIN_BRACKETS")
                consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", RobotBundle.message("formatter.space.around.assignment.operators"))
                consumer.renameStandardOption("SPACE_WITHIN_BRACKETS", RobotBundle.message("formatter.space.within.variables.array"))
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "KEEP_ADDITIONAL_SPACES_AFTER_VARIABLE_ASSIGNMENTS",
                    RobotBundle.message("formatter.keep.additional.spaces.after.variable.assignments"),
                    customizableOptions.SPACES_OTHER)
                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "KEEP_ADDITIONAL_SPACES_BETWEEN_TEMPLATE_VALUES",
                    RobotBundle.message("formatter.keep.additional.spaces.between.template.values"),
                    customizableOptions.SPACES_OTHER)
            }

            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> {
                consumer.showStandardOptions("RIGHT_MARGIN",
                    "KEEP_LINE_BREAKS",
                    "WRAP_LONG_LINES",
                    "CALL_PARAMETERS_WRAP",
                    "METHOD_PARAMETERS_WRAP",
                    "FOR_STATEMENT_WRAP")
                consumer.renameStandardOption("METHOD_PARAMETERS_WRAP", RobotBundle.message("formatter.wrap.keyword.definition.arguments"))
                consumer.renameStandardOption("CALL_PARAMETERS_WRAP", RobotBundle.message("formatter.wrap.keyword.call.arguments"))
                consumer.renameStandardOption("FOR_STATEMENT_WRAP", RobotBundle.message("formatter.for.statement.wrapping.expression"))

                consumer.showCustomOption(RobotCodeStyleSettings::class.java,
                    "ALIGN_CONTINUATION_WITH_VARIABLE_DEFINITION",
                    RobotBundle.message("formatter.align.continuation.with.variable.definition"),
                    customizableOptions.WRAPPING_METHOD_ARGUMENTS_WRAPPING)
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
            }

            else -> { // Ignore
            }
        }
    }

    override fun getCodeSample(settingsType: SettingsType): String {
        return """*** Settings ***
Documentation      Formatter showcase with global settings, long arguments and keyword calls
Library            Collections
Library            OperatingSystem             WITH NAME    OS
Library            SomeReallyLongLibraryName   alias=LongLib    endpoint=https://very.long.endpoint.example.local/api/v1/robot/formatter
Resource           resources/common.resource
Variables          resources/variables.py
Metadata           owner    qa-team-platform
Metadata           long_meta_key    This metadata value is intentionally very long to trigger wrapping behavior based on RIGHT_MARGIN
Suite Setup        Initialize Suite Context    env=dev    browser=chromium    region=eu-central-1    retries=3    timeout=120s
Suite Teardown     Cleanup Suite Context       keepLogs=${'$'}{TRUE}    uploadResults=${'$'}{FALSE}    reason=normal-shutdown
Test Setup         Prepare Shared Fixture      user=robot_admin    password=${'$'}{SECRET}    locale=de_DE    timezone=Europe/Berlin
Test Teardown      Release Shared Fixture      force=${'$'}{TRUE}    retry=2
Default Tags       smoke    ui    regression    formatter    very_long_tag_to_force_wrap
Force Tags         release_candidate    nightly_build    pipeline_stage_validation
    
*** Variables ***
${'$'}{BASE_URL}            https://example.test.local/application/root/path
${'$'}{LONG_TEXT}           This is a very long text value that can exceed the configured right margin and trigger wrapping when used in calls
@{LIST}                     one    two    three    four    five    six    seven
&{USER}                     name=robot    role=admin    active=${'$'}{TRUE}
    
*** Test Cases ***
Formatter Options Showcase
    [Documentation]    This local setting is intentionally very long so local setting wrapping and first argument on new line can be inspected in the preview
    
    
    [Tags]    smoke    formatter    local_setting_wrap_demo    right_margin_sensitive
    [Timeout]    2 minutes
    [Setup]    Open Browser With Extended Options    ${'$'}{BASE_URL}    browser=chromium    headless=${'$'}{TRUE}    viewport=1920x1080
    
    ${'$'}{result}=       Execute Complex Keyword Chain    ${'$'}{LONG_TEXT}    level=1    retry=3    mode=fast
    ...                   nested=true    preserveState=${'$'}{TRUE}    output=full
    
    Should Be Equal    ${'$'}{result}    expected_value
    Run Keyword If    '${'$'}{USER}[role]' == 'admin'    Log    Admin execution path enabled
    
    FOR    ${'$'}{index}    IN RANGE    5  20  5
        Log Many    loop    value    ${'$'}{index}    ${'$'}{LONG_TEXT}
    END
    
    WHILE    ${'$'}{index} < 20    limit=100    on_limit=pass    on_limit_message=Reached loop limit in formatter sample
        ${'$'}{index}=    Evaluate    ${'$'}{index} + 1
        Log    Current index is ${'$'}{index}
    END
    
    [Teardown]    Close Browser And Collect Artifacts    screenshot=${'$'}{TRUE}    logs=${'$'}{TRUE}    traces=${'$'}{TRUE}
    
Template Spacing Showcase
    [Template]      Validate Pair With Optional Context
    alpha           beta           optional=true
    gamma             delta         optional=false
    epsilon         zeta             optional=true
    
*** Keywords ***
Initialize Suite Context
    [Arguments]    ${'$'}{env}    ${'$'}{browser}=chromium    ${'$'}{region}=eu-central-1    ${'$'}{retries}=1    ${'$'}{timeout}=60s
    Log    Initializing suite in ${'$'}{env} using ${'$'}{browser} at ${'$'}{region}
    
Execute Complex Keyword Chain
    [Arguments]    ${'$'}{text}    ${'$'}{level}=0    ${'$'}{retry}=1    ${'$'}{mode}=normal    ${'$'}{nested}=${'$'}{FALSE}    ${'$'}{preserveState}=${'$'}{FALSE}    ${'$'}{output}=summary
    ${'$'}{prepared}=    Prepare Payload For Execution    ${'$'}{text}    level=${'$'}{level}    mode=${'$'}{mode}
    ${'$'}{handled}=     Nested Dispatcher Keyword    ${'$'}{prepared}    retry=${'$'}{retry}    nested=${'$'}{nested}    preserveState=${'$'}{preserveState}
    [Return]    ${'$'}{handled}
    
Nested Dispatcher Keyword
    [Arguments]    ${'$'}{payload}    ${'$'}{retry}=1    ${'$'}{nested}=${'$'}{FALSE}    ${'$'}{preserveState}=${'$'}{FALSE}
    Run Keyword If    ${'$'}{nested}    Call Inner Worker Keyword    ${'$'}{payload}    retry=${'$'}{retry}    preserveState=${'$'}{preserveState}
    ...    ELSE    Log    Nested mode disabled
    [Return]    expected_value
    
Call Inner Worker Keyword
    [Arguments]    ${'$'}{payload}    ${'$'}{retry}=1    ${'$'}{preserveState}=${'$'}{FALSE}
    Log Many    worker    payload    ${'$'}{payload}    retry    ${'$'}{retry}    preserve    ${'$'}{preserveState}
    
Validate Pair With Optional Context
    [Arguments]    ${'$'}{left}    ${'$'}{right}    ${'$'}{optional}=${'$'}{FALSE}
    Should Not Be Empty    ${'$'}{left}
    Should Not Be Empty    ${'$'}{right}
"""
    }
}
