package dev.xeonkryptos.xeonrobotframeworkplugin.config;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ExtendedRobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLexerAdapter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RobotHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey SECTION_TITLE = TextAttributesKey.createTextAttributesKey("ROBOT_SECTION_TITLE",
                                                                                                    DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey GLOBAL_SETTING_OPTION = TextAttributesKey.createTextAttributesKey("ROBOT_GLOBAL_SETTING_OPTION",
                                                                                                            DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
    public static final TextAttributesKey LOCAL_SETTING_OPTION = TextAttributesKey.createTextAttributesKey("ROBOT_LOCAL_SETTING_OPTION",
                                                                                                           DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey IMPORT = TextAttributesKey.createTextAttributesKey("ROBOT_IMPORT_OPTION",
                                                                                             DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
    public static final TextAttributesKey IMPORT_ARGUMENT = TextAttributesKey.createTextAttributesKey("ROBOT_IMPORT_ARGUMENT",
                                                                                                      DefaultLanguageHighlighterColors.HIGHLIGHTED_REFERENCE);
    public static final TextAttributesKey USER_KEYWORD_NAME = TextAttributesKey.createTextAttributesKey("ROBOT_USER_KEYWORD_DEFINITION",
                                                                                                        DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey TEST_CASE_NAME = TextAttributesKey.createTextAttributesKey("ROBOT_TEST_CASE_NAME",
                                                                                                     DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey TASK_NAME = TextAttributesKey.createTextAttributesKey("ROBOT_TASK_NAME", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("ROBOT_KEYWORD_CALL",
                                                                                              DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey PARAMETER = TextAttributesKey.createTextAttributesKey("ROBOT_PARAMETER", DefaultLanguageHighlighterColors.PARAMETER);
    public static final TextAttributesKey ARGUMENT = TextAttributesKey.createTextAttributesKey("ROBOT_POSITIONAL_ARGUMENT",
                                                                                               DefaultLanguageHighlighterColors.STATIC_FIELD);
    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey("ROBOT_VARIABLE",
                                                                                               DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP);
    public static final TextAttributesKey EXTENDED_VARIABLE_ACCESS_BRACKETS = TextAttributesKey.createTextAttributesKey(
            "ROBOT_EXTENDED_VARIABLE_ACCESS_BRACKETS",
            DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("ROBOT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey GHERKIN = TextAttributesKey.createTextAttributesKey("ROBOT_GHERKIN_KEYWORD",
                                                                                              DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey STRUCTURAL_KEYWORDS = TextAttributesKey.createTextAttributesKey("ROBOT_STRUCTURAL_KEYWORD",
                                                                                                          DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey PYTHON_EXPRESSION_CONTENT = TextAttributesKey.createTextAttributesKey("ROBOT_PYTHON_EXPRESSION_CONTENT",
                                                                                                                DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey ERROR = TextAttributesKey.createTextAttributesKey(TokenType.BAD_CHARACTER.toString(),
                                                                                            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);

    public static final TextAttributesKey REASSIGNED_VARIABLE = TextAttributesKey.createTextAttributesKey("ROBOT_REASSIGNED_VARIABLE", VARIABLE);

    private static final Map<IElementType, TextAttributesKey> keys = new HashMap<>();

    static {
        keys.put(RobotTypes.SETTINGS_HEADER, SECTION_TITLE);
        keys.put(RobotTypes.VARIABLES_HEADER, SECTION_TITLE);
        keys.put(RobotTypes.USER_KEYWORDS_HEADER, SECTION_TITLE);
        keys.put(RobotTypes.COMMENTS_HEADER, SECTION_TITLE);
        keys.put(RobotTypes.TEST_CASES_HEADER, SECTION_TITLE);
        keys.put(RobotTypes.TASKS_HEADER, SECTION_TITLE);

        keys.put(RobotTypes.USER_KEYWORD_NAME, USER_KEYWORD_NAME);
        keys.put(RobotTypes.TEST_CASE_NAME, TEST_CASE_NAME);
        keys.put(RobotTypes.TASK_NAME, TASK_NAME);

        keys.put(RobotTypes.COMMENT, COMMENT);
        keys.put(RobotTypes.PARAMETER_NAME, PARAMETER);
        keys.put(RobotTypes.TEMPLATE_PARAMETER_NAME, PARAMETER);
        keys.put(RobotTypes.LITERAL_CONSTANT, ARGUMENT);
        keys.put(RobotTypes.TEMPLATE_ARGUMENT_VALUE, ARGUMENT);

        keys.put(RobotTypes.GIVEN, GHERKIN);
        keys.put(RobotTypes.WHEN, GHERKIN);
        keys.put(RobotTypes.THEN, GHERKIN);
        keys.put(RobotTypes.AND, GHERKIN);
        keys.put(RobotTypes.BUT, GHERKIN);

        keys.put(RobotTypes.FOR, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.FOR_IN, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.WHILE, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.TRY, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.EXCEPT, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.FINALLY, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.RETURN, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.GROUP, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.IF, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.ELSE_IF, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.ELSE, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.END, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.BREAK, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.CONTINUE, STRUCTURAL_KEYWORDS);
        keys.put(RobotTypes.WITH_NAME, STRUCTURAL_KEYWORDS);

        keys.put(RobotTypes.PYTHON_EXPRESSION_CONTENT, PYTHON_EXPRESSION_CONTENT);

        keys.put(RobotTypes.SCALAR_VARIABLE_START, VARIABLE);
        keys.put(RobotTypes.LIST_VARIABLE_START, VARIABLE);
        keys.put(RobotTypes.DICT_VARIABLE_START, VARIABLE);
        keys.put(RobotTypes.ENV_VARIABLE_START, VARIABLE);
        keys.put(RobotTypes.VARIABLE_BODY, VARIABLE);
        keys.put(RobotTypes.VARIABLE_END, VARIABLE);
        keys.put(ExtendedRobotTypes.EXTENDED_VARIABLE_ACCESS_BODY, VARIABLE);
        keys.put(RobotTypes.VARIABLE_ACCESS_START, EXTENDED_VARIABLE_ACCESS_BRACKETS);
        keys.put(RobotTypes.VARIABLE_ACCESS_END, EXTENDED_VARIABLE_ACCESS_BRACKETS);
        keys.put(RobotTypes.USER_KEYWORD_STATEMENT, USER_KEYWORD_NAME);
        keys.put(RobotTypes.KEYWORD_LIBRARY_NAME, KEYWORD);
        keys.put(RobotTypes.KEYWORD_LIBRARY_SEPARATOR, KEYWORD);
        keys.put(RobotTypes.KEYWORD_NAME, KEYWORD);

        keys.put(RobotTypes.SUITE_NAME_KEYWORD, GLOBAL_SETTING_OPTION);
        keys.put(RobotTypes.DOCUMENTATION_KEYWORD, GLOBAL_SETTING_OPTION);
        keys.put(RobotTypes.METADATA_KEYWORD, GLOBAL_SETTING_OPTION);
        keys.put(RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS, GLOBAL_SETTING_OPTION);
        keys.put(RobotTypes.TAGS_KEYWORDS, GLOBAL_SETTING_OPTION);
        keys.put(RobotTypes.TEMPLATE_KEYWORDS, GLOBAL_SETTING_OPTION);
        keys.put(RobotTypes.TIMEOUT_KEYWORDS, GLOBAL_SETTING_OPTION);
        keys.put(RobotTypes.UNKNOWN_SETTING_KEYWORD, GLOBAL_SETTING_OPTION);

        keys.put(RobotTypes.LOCAL_SETTING_START, LOCAL_SETTING_OPTION);
        keys.put(RobotTypes.LOCAL_SETTING_END, LOCAL_SETTING_OPTION);
        keys.put(RobotTypes.LOCAL_SETTING_NAME, LOCAL_SETTING_OPTION);

        keys.put(RobotTypes.LIBRARY_IMPORT_KEYWORD, IMPORT);
        keys.put(RobotTypes.RESOURCE_IMPORT_KEYWORD, IMPORT);
        keys.put(RobotTypes.VARIABLES_IMPORT_KEYWORD, IMPORT);

        keys.put(TokenType.BAD_CHARACTER, ERROR);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new RobotLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return pack(keys.get(tokenType));
    }
}
