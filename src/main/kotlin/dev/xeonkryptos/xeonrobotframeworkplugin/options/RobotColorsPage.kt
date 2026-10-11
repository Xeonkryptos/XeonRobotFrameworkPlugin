package dev.xeonkryptos.xeonrobotframeworkplugin.options

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes.RobotHighlighter
import dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes.RobotFeatureFileType
import javax.swing.Icon

class RobotColorsPage : ColorSettingsPage {

    override fun getIcon(): Icon? = RobotFeatureFileType.getInstance().icon

    override fun getHighlighter(): SyntaxHighlighter = RobotHighlighter()

    override fun getDemoText(): String {
        return """
               Ignored
               *** Settings ***
               Documentation     This is some demo text
               Library           CalculatorLibrary
               
               *** Variables ***
               ${'$'}{var1}  12345
               ${'$'}{var2}  another variable
               ${'$'}{var3: int}  12345
               ${'$'}{var4: list[int]}  1  2  3
               ${'$'}{var5: int | str}  12345

               *** Test Cases ***
               Addition
                 [Tags]  Calculator
                   Given  calculator has been cleared
                   When user types "1 + 1"
                   And  user pushes equals
                   Then  result is "2"
               
               #Subtraction
               #  [Tags]  Calculator
               #    TODO: implement me
               
               *** Keywords ***
               Calculator has been cleared
                   Push button    C
               
               IF  '${'$'}{variable}' == 'B'
                   Push Button    A
               END
               
               User types "${'$'}{expression}"
                   Push buttons    ${'$'}{expression}
               
               User pushes equals
                   Push button    =
               
               Result is "${'$'}{result}"
                   Result should be    ${'$'}{result}
                   Log    ${'$'}{calculator.history(1, 'last', prefix${'$'}{suffix})}
                   """.trimIndent()
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String?, TextAttributesKey?>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRIBUTES

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = RobotBundle.message("options.entrypoint")
}

private val ATTRIBUTES = arrayOf(AttributesDescriptor(RobotBundle.message("color.settings.sectionHeader"), RobotHighlighter.SECTION_TITLE),
    AttributesDescriptor(RobotBundle.message("color.settings.comment"), RobotHighlighter.COMMENT),
    AttributesDescriptor(RobotBundle.message("color.settings.parameter"), RobotHighlighter.PARAMETER),
    AttributesDescriptor(RobotBundle.message("color.settings.argument"), RobotHighlighter.ARGUMENT),
    AttributesDescriptor(RobotBundle.message("color.settings.gherkin"), RobotHighlighter.GHERKIN),
    AttributesDescriptor(RobotBundle.message("color.settings.syntaxMarker"), RobotHighlighter.STRUCTURAL_KEYWORDS),
    AttributesDescriptor(RobotBundle.message("color.settings.variable"), RobotHighlighter.VARIABLE),
    AttributesDescriptor(RobotBundle.message("color.settings.variableDataType"), RobotHighlighter.VARIABLE_DATA_TYPE),
    AttributesDescriptor(RobotBundle.message("color.settings.variableDataTypeMarker"), RobotHighlighter.VARIABLE_DATA_TYPE_MARKER),
    AttributesDescriptor(RobotBundle.message("color.settings.variableExtendedAccessOperator"), RobotHighlighter.VARIABLE_EXTENDED_ACCESS_OPERATOR),
    AttributesDescriptor(RobotBundle.message("color.settings.variableMethodCall"), RobotHighlighter.VARIABLE_METHOD_CALL),
    AttributesDescriptor(RobotBundle.message("color.settings.variableMethodCallParentheses"), RobotHighlighter.VARIABLE_METHOD_CALL_PARENTHESES),
    AttributesDescriptor(RobotBundle.message("color.settings.variableMethodCallComma"), RobotHighlighter.VARIABLE_METHOD_CALL_COMMA),
    AttributesDescriptor(RobotBundle.message("color.settings.keyword"), RobotHighlighter.KEYWORD),
    AttributesDescriptor(RobotBundle.message("color.settings.userKeywordName"), RobotHighlighter.USER_KEYWORD_NAME),
    AttributesDescriptor(RobotBundle.message("color.settings.testCaseName"), RobotHighlighter.TEST_CASE_NAME),
    AttributesDescriptor(RobotBundle.message("color.settings.taskName"), RobotHighlighter.TASK_NAME),
    AttributesDescriptor(RobotBundle.message("color.settings.bracketSetting"), RobotHighlighter.LOCAL_SETTING_OPTION),
    AttributesDescriptor(RobotBundle.message("color.settings.setting"), RobotHighlighter.GLOBAL_SETTING_OPTION),
    AttributesDescriptor(RobotBundle.message("color.settings.pythonExpression"), RobotHighlighter.PYTHON_EXPRESSION_CONTENT))
