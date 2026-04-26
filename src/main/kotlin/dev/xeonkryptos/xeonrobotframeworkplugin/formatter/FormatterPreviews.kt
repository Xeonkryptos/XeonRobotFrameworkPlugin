package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

object FormatterPreviews {
    const val INDENT = """*** Test Cases ***
Test Case
    FOR    ${'$'}{index}    IN RANGE    10
        IF    ${'$'}{index} > 5
            WHILE    ${'$'}{index} < 8
                Log    Nested structure with continuation
                ...                    and multiple lines
                ...                    showing indent levels
            END
        END
    END

*** Keywords ***
Keyword With Nested Blocks
    [Arguments]    ${'$'}{arg1}    ${'$'}{arg2}=default
    TRY
        FOR    ${'$'}{item}    IN    @{LIST}
            Log    Processing ${'$'}{item}
        END
    EXCEPT    Error
        Log    Handle error
    END
"""

    const val SPACING = """*** Variables ***
${'$'}{VAR} =    value
@{LIST} =    one    two    three
&{DICT} =    key=value

${'$'}{VAR_WITH_BRACES}    {'key': 'value'}
${'$'}{VAR_WITH_BRACKETS}    [1, 2, 3]

*** Test Cases ***
Test
    [Arguments]    ${'$'}{arg}    ${'$'}{opt}=default
    ${'$'}{val}=    Set Variable    [1, 2, 3]
    Log    ${'$'}{val}[0]
"""

    const val WRAPPING_AND_BRACES = """*** Settings ***
Documentation      Shows wrapping behavior for global settings
Library            VeryLongLibraryNameThatExceedsTheLineLimit    alias=Short    endpoint=https://very.long.endpoint.example.com/api/v1/resource
Metadata           very_long_metadata_key_that_exceeds_line_limit    This is a long value
Suite Setup        VeryLongKeywordNameWithManyArguments    arg1    arg2    arg3    arg4    arg5
Test Template      Keyword With Long Arguments

*** Variables ***
${'$'}{SHORT}    short
${'$'}{LONG}    This is a very long variable definition that exceeds the line limit and should trigger wrapping behavior when configured
@{LIST}    one    two    three    four    five    six    seven    eight

*** Test Cases ***        Column 1     Column 2   Column 3              Column 4    Column 5
Data Driven Test Case 1  arg1    arg2    arg3    arg4    arg5    arg6
Data Driven Test Case With a longer name  arg1    longer argument    arg3    arg4    arg5    arg6

Template Spacing Showcase
    [Template]      Validate Pair With Optional Context
    alpha           beta           optional=true
    gamma             delta         optional=false
    epsilon         zeta             optional=true

Test Case With Local Settings
    [Template]  NONE
    [Documentation]    Shows local settings wrapping behavior
    [Tags]    smoke    regression    very_long_tag_name_that_causes_wrapping    another_long_tag
    [Timeout]    5 minutes    message=This is a very long timeout message that exceeds the line limit
    [Setup]    VeryLongSetupKeywordNameWithMultipleArguments    arg1    arg2    arg3    arg4

    VeryLongKeywordNameWithManyArguments    arg1    arg2    arg3    arg4    arg5    arg6
    ${'$'}{result}=    ComplexKeywordChainWithMultipleParameters    param1    param2    param3    param4
    ...                    param5    param6    param7

    FOR    ${'$'}{index}    IN RANGE    1    10    2
        Log    Iteration ${'$'}{index}
    END

    WHILE    ${'$'}{condition}    limit=100    on_limit=pass    on_limit_message=Limit reached
        Log    Inside while loop
    END

*** Keywords ***
Keyword With Long Arguments
    [Arguments]    ${'$'}{arg1}    ${'$'}{arg2}=default    ${'$'}{arg3}=value    ${'$'}{arg4}    ${'$'}{arg5}=test
    Log    ${'$'}{arg1}
    
Validate Pair With Optional Context
    [Arguments]    ${'$'}{'$'}{left}    ${'$'}{'$'}{right}    ${'$'}{'$'}{optional}=${'$'}{'$'}{FALSE}
    Should Not Be Empty    ${'$'}{'$'}{left}
    Should Not Be Empty    ${'$'}{'$'}{right}
"""

    const val BLANK_LINES = """*** Settings ***
Documentation      Shows blank lines around settings

Suite Setup        Initialize Suite
Suite Teardown     Cleanup Suite

Metadata           owner    qa-team
Metadata           version    1.0.0

Library            Collections

Resource           resources/common.resource

*** Variables ***

${'$'}{VAR}    value

*** Test Cases ***
Test With Local Settings
    [Documentation]    Shows blank lines after local settings
    [Tags]    smoke
    [Timeout]    1 minute

    Log    Test content
"""
}
