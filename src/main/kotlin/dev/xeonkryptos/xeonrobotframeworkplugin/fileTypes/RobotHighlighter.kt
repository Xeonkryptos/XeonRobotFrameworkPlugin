package dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.project.Project
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexerAdapter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ExtendedRobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes

class RobotHighlighter @JvmOverloads constructor(private val project: Project? = null) : SyntaxHighlighterBase() {

    companion object {
        @JvmField
        val SECTION_TITLE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_SECTION_TITLE", DefaultLanguageHighlighterColors.STRING)

        @JvmField
        val GLOBAL_SETTING_OPTION: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_GLOBAL_SETTING_OPTION", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE)

        @JvmField
        val LOCAL_SETTING_OPTION: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_LOCAL_SETTING_OPTION", DefaultLanguageHighlighterColors.METADATA)

        @JvmField
        val IMPORT_ARGUMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_IMPORT_ARGUMENT", DefaultLanguageHighlighterColors.HIGHLIGHTED_REFERENCE)

        @JvmField
        val USER_KEYWORD_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_USER_KEYWORD_DEFINITION", DefaultLanguageHighlighterColors.KEYWORD)

        @JvmField
        val TEST_CASE_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_TEST_CASE_NAME", DefaultLanguageHighlighterColors.KEYWORD)

        @JvmField
        val TASK_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_TASK_NAME", DefaultLanguageHighlighterColors.KEYWORD)

        @JvmField
        val KEYWORD: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_KEYWORD_CALL", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)

        @JvmField
        val PARAMETER: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_PARAMETER", DefaultLanguageHighlighterColors.PARAMETER)

        @JvmField
        val ARGUMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_POSITIONAL_ARGUMENT", DefaultLanguageHighlighterColors.STATIC_FIELD)

        @JvmField
        val VARIABLE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_VARIABLE", DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP)
        val EXTENDED_VARIABLE_ACCESS_BRACKETS: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_EXTENDED_VARIABLE_ACCESS_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)

        @JvmField
        val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

        @JvmField
        val GHERKIN: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_GHERKIN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)

        @JvmField
        val STRUCTURAL_KEYWORDS: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_STRUCTURAL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)

        @JvmField
        val PYTHON_EXPRESSION_CONTENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_PYTHON_EXPRESSION_CONTENT", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR)
        val ERROR: TextAttributesKey = TextAttributesKey.createTextAttributesKey(TokenType.BAD_CHARACTER.toString(), DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE)

        @JvmField
        val REASSIGNED_VARIABLE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("ROBOT_REASSIGNED_VARIABLE", VARIABLE)

        private val keys: MutableMap<IElementType?, TextAttributesKey?> = mutableMapOf()

        init {
            keys[RobotTypes.SETTINGS_HEADER] = SECTION_TITLE
            keys[RobotTypes.VARIABLES_HEADER] = SECTION_TITLE
            keys[RobotTypes.USER_KEYWORDS_HEADER] = SECTION_TITLE
            keys[RobotTypes.COMMENTS_HEADER] = SECTION_TITLE
            keys[RobotTypes.TEST_CASES_HEADER_NAME] = SECTION_TITLE
            keys[RobotTypes.TASKS_HEADER_NAME] = SECTION_TITLE
            keys[RobotTypes.TASKS_HEADER] = SECTION_TITLE

            keys[RobotTypes.DATA_DRIVEN_COLUMN_NAME] = SECTION_TITLE

            keys[RobotTypes.USER_KEYWORD_NAME_PART] = USER_KEYWORD_NAME
            keys[RobotTypes.TEST_CASE_NAME_PART] = TEST_CASE_NAME
            keys[RobotTypes.TASK_NAME_PART] = TASK_NAME

            keys[RobotTypes.COMMENT] = COMMENT
            keys[RobotTypes.PARAMETER_NAME] = PARAMETER
            keys[RobotTypes.TEMPLATE_PARAMETER_NAME] = PARAMETER
            keys[RobotTypes.LITERAL_CONSTANT] = ARGUMENT
            keys[RobotTypes.TEMPLATE_ARGUMENT_VALUE] = ARGUMENT

            keys[RobotTypes.GIVEN] = GHERKIN
            keys[RobotTypes.WHEN] = GHERKIN
            keys[RobotTypes.THEN] = GHERKIN
            keys[RobotTypes.AND] = GHERKIN
            keys[RobotTypes.BUT] = GHERKIN

            keys[RobotTypes.VAR] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.FOR] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.FOR_IN] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.FOR_IN_ENUMERATE] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.FOR_IN_RANGE] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.FOR_IN_ZIP] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.WHILE] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.TRY] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.EXCEPT] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.FINALLY] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.RETURN] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.GROUP] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.IF] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.ELSE_IF] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.ELSE] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.END] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.BREAK] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.CONTINUE] = STRUCTURAL_KEYWORDS
            keys[RobotTypes.WITH_NAME] = STRUCTURAL_KEYWORDS

            keys[RobotTypes.PYTHON_EXPRESSION_CONTENT] = PYTHON_EXPRESSION_CONTENT

            keys[RobotTypes.SCALAR_VARIABLE_START] = VARIABLE
            keys[RobotTypes.LIST_VARIABLE_START] = VARIABLE
            keys[RobotTypes.DICT_VARIABLE_START] = VARIABLE
            keys[RobotTypes.ENV_VARIABLE_START] = VARIABLE
            keys[RobotTypes.VARIABLE_BODY] = VARIABLE
            keys[RobotTypes.VARIABLE_LBRACE] = VARIABLE
            keys[RobotTypes.VARIABLE_RBRACE] = VARIABLE
            keys[RobotTypes.PYTHON_EXPRESSION_START] = VARIABLE
            keys[RobotTypes.PYTHON_EXPRESSION_END] = VARIABLE
            keys[ExtendedRobotTypes.EXTENDED_VARIABLE_ACCESS_BODY] = VARIABLE
            keys[RobotTypes.VARIABLE_ACCESS_START] = EXTENDED_VARIABLE_ACCESS_BRACKETS
            keys[RobotTypes.VARIABLE_ACCESS_END] = EXTENDED_VARIABLE_ACCESS_BRACKETS
            keys[RobotTypes.USER_KEYWORD_STATEMENT] = USER_KEYWORD_NAME
            keys[RobotTypes.KEYWORD_LIBRARY_NAME] = KEYWORD
            keys[RobotTypes.KEYWORD_LIBRARY_SEPARATOR] = KEYWORD
            keys[RobotTypes.KEYWORD_NAME] = KEYWORD

            keys[RobotTypes.SUITE_NAME_KEYWORD] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.DOCUMENTATION_KEYWORD] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.METADATA_KEYWORD] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.TAGS_KEYWORDS] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.TEMPLATE_KEYWORDS] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.TIMEOUT_KEYWORDS] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.UNKNOWN_SETTING_KEYWORD] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.LIBRARY_IMPORT_KEYWORD] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.RESOURCE_IMPORT_KEYWORD] = GLOBAL_SETTING_OPTION
            keys[RobotTypes.VARIABLES_IMPORT_KEYWORD] = GLOBAL_SETTING_OPTION

            keys[RobotTypes.LOCAL_SETTING_START] = LOCAL_SETTING_OPTION
            keys[RobotTypes.LOCAL_SETTING_END] = LOCAL_SETTING_OPTION
            keys[RobotTypes.LOCAL_SETTING_NAME] = LOCAL_SETTING_OPTION

            keys[TokenType.BAD_CHARACTER] = ERROR
        }
    }

    override fun getHighlightingLexer(): Lexer = RobotLexerAdapter(project)

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = pack(keys[tokenType])
}
