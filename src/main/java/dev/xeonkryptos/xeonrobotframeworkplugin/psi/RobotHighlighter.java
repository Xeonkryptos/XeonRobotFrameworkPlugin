package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RobotHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> keys1 = new HashMap<>();

    public static final TextAttributesKey HEADING = TextAttributesKey.createTextAttributesKey("Section Heading", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey SETTING = TextAttributesKey.createTextAttributesKey("Global Setting Option",
                                                                                              DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
    public static final TextAttributesKey LOCAL_SETTING = TextAttributesKey.createTextAttributesKey("Local Setting Option",
                                                                                                    DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
    public static final TextAttributesKey IMPORT = TextAttributesKey.createTextAttributesKey("Import Option",
                                                                                             DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
    public static final TextAttributesKey IMPORT_ARGUMENT = TextAttributesKey.createTextAttributesKey("Import Argument",
                                                                                                      DefaultLanguageHighlighterColors.HIGHLIGHTED_REFERENCE);
    public static final TextAttributesKey REASSIGNED_VARIABLE = TextAttributesKey.createTextAttributesKey("Reassigned Variable",
                                                                                                          DefaultLanguageHighlighterColors.HIGHLIGHTED_REFERENCE);
    public static final TextAttributesKey KEYWORD_DEFINITION = TextAttributesKey.createTextAttributesKey("User Keyword Definition",
                                                                                                         DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("Keyword Usage", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey PARAMETER = TextAttributesKey.createTextAttributesKey("Parameter", DefaultLanguageHighlighterColors.PARAMETER);
    public static final TextAttributesKey ARGUMENT = TextAttributesKey.createTextAttributesKey("Positional Argument",
                                                                                               DefaultLanguageHighlighterColors.STATIC_FIELD);
    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey("Variable Usage",
                                                                                               DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP);
    public static final TextAttributesKey VARIABLE_DEFINITION = TextAttributesKey.createTextAttributesKey("Variable Definition",
                                                                                                          DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP);
    public static final TextAttributesKey EXTENDED_VARIABLE_ACCESS_BRACKETS = TextAttributesKey.createTextAttributesKey("Extended Variable Brackets",
                                                                                                                        DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("Comment", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey GHERKIN = TextAttributesKey.createTextAttributesKey("Gherkin Keywords", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey SYNTAX_MARKER = TextAttributesKey.createTextAttributesKey("Robot Keyword", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey ERROR = TextAttributesKey.createTextAttributesKey(TokenType.BAD_CHARACTER.toString(),
                                                                                            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new RobotLexerAdapter();
    }

    static {
        keys1.put(RobotTypes.SETTINGS_HEADER, HEADING);
        keys1.put(RobotTypes.VARIABLES_HEADER, HEADING);
        keys1.put(RobotTypes.USER_KEYWORDS_HEADER, HEADING);
        keys1.put(RobotTypes.COMMENTS_HEADER, HEADING);
        keys1.put(RobotTypes.TEST_CASES_HEADER, HEADING);
        keys1.put(RobotTypes.TASKS_HEADER, HEADING);

        keys1.put(RobotTypes.USER_KEYWORD_NAME, KEYWORD_DEFINITION);
        keys1.put(RobotTypes.TEST_CASE_NAME, KEYWORD_DEFINITION);
        keys1.put(RobotTypes.TASK_NAME, KEYWORD_DEFINITION);

        keys1.put(RobotTypes.COMMENT, COMMENT);
        keys1.put(RobotTypes.PARAMETER_NAME, PARAMETER);
        keys1.put(RobotTypes.TEMPLATE_PARAMETER_NAME, PARAMETER);
        keys1.put(RobotTypes.LITERAL_CONSTANT, ARGUMENT);
        keys1.put(RobotTypes.TEMPLATE_ARGUMENT_VALUE, ARGUMENT);

        keys1.put(RobotTypes.GIVEN, GHERKIN);
        keys1.put(RobotTypes.WHEN, GHERKIN);
        keys1.put(RobotTypes.THEN, GHERKIN);
        keys1.put(RobotTypes.AND, GHERKIN);
        keys1.put(RobotTypes.BUT, GHERKIN);

        keys1.put(RobotTypes.FOR, SYNTAX_MARKER);
        keys1.put(RobotTypes.FOR_IN, SYNTAX_MARKER);
        keys1.put(RobotTypes.WHILE, SYNTAX_MARKER);
        keys1.put(RobotTypes.TRY, SYNTAX_MARKER);
        keys1.put(RobotTypes.EXCEPT, SYNTAX_MARKER);
        keys1.put(RobotTypes.FINALLY, SYNTAX_MARKER);
        keys1.put(RobotTypes.RETURN, SYNTAX_MARKER);
        keys1.put(RobotTypes.GROUP, SYNTAX_MARKER);
        keys1.put(RobotTypes.IF, SYNTAX_MARKER);
        keys1.put(RobotTypes.ELSE_IF, SYNTAX_MARKER);
        keys1.put(RobotTypes.ELSE, SYNTAX_MARKER);
        keys1.put(RobotTypes.END, SYNTAX_MARKER);
        keys1.put(RobotTypes.BREAK, SYNTAX_MARKER);
        keys1.put(RobotTypes.CONTINUE, SYNTAX_MARKER);
        keys1.put(RobotTypes.WITH_NAME, SYNTAX_MARKER);

        keys1.put(RobotTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        keys1.put(RobotTypes.SCALAR_VARIABLE_START, VARIABLE);
        keys1.put(RobotTypes.LIST_VARIABLE_START, VARIABLE);
        keys1.put(RobotTypes.DICT_VARIABLE_START, VARIABLE);
        keys1.put(RobotTypes.ENV_VARIABLE_START, VARIABLE);
        keys1.put(RobotTypes.VARIABLE_BODY, VARIABLE);
        keys1.put(RobotTypes.VARIABLE_END, VARIABLE);
        keys1.put(RobotTypes.VARIABLE_KEY_ACCESS, VARIABLE);
        keys1.put(RobotTypes.VARIABLE_SLICE_ACCESS, VARIABLE);
        keys1.put(RobotTypes.VARIABLE_INDEX_ACCESS, VARIABLE);
        keys1.put(RobotTypes.VARIABLE_BODY_EXTENSION, VARIABLE);
        keys1.put(RobotTypes.VARIABLE_ACCESS_START, EXTENDED_VARIABLE_ACCESS_BRACKETS);
        keys1.put(RobotTypes.VARIABLE_ACCESS_END, EXTENDED_VARIABLE_ACCESS_BRACKETS);
        keys1.put(RobotTypes.USER_KEYWORD_STATEMENT, KEYWORD_DEFINITION);
        keys1.put(RobotTypes.KEYWORD_LIBRARY_NAME, KEYWORD);
        keys1.put(RobotTypes.KEYWORD_LIBRARY_SEPARATOR, KEYWORD);
        keys1.put(RobotTypes.KEYWORD_NAME, KEYWORD);

        keys1.put(RobotTypes.SUITE_NAME_KEYWORD, SETTING);
        keys1.put(RobotTypes.DOCUMENTATION_KEYWORD, SETTING);
        keys1.put(RobotTypes.METADATA_KEYWORD, SETTING);
        keys1.put(RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS, SETTING);
        keys1.put(RobotTypes.TAGS_KEYWORDS, SETTING);
        keys1.put(RobotTypes.TEMPLATE_KEYWORDS, SETTING);
        keys1.put(RobotTypes.TIMEOUT_KEYWORDS, SETTING);
        keys1.put(RobotTypes.UNKNOWN_SETTING_KEYWORD, SETTING);

        keys1.put(RobotTypes.LOCAL_SETTING_NAME, LOCAL_SETTING);
        keys1.put(RobotTypes.ARGUMENTS_SETTING_NAME, LOCAL_SETTING);

        keys1.put(RobotTypes.LIBRARY_IMPORT_KEYWORD, IMPORT);
        keys1.put(RobotTypes.RESOURCE_IMPORT_KEYWORD, IMPORT);
        keys1.put(RobotTypes.VARIABLES_IMPORT_KEYWORD, IMPORT);

        keys1.put(TokenType.BAD_CHARACTER, ERROR);
    }

    @NotNull
    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return pack(keys1.get(tokenType));
    }
}
