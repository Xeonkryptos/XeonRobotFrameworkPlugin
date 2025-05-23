package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RobotHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> keys1 = new HashMap<>();

    public static final TextAttributesKey HEADING = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.HEADING.toString(),
                                                                                              DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey SETTING = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.SETTING.toString(),
                                                                                              DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
    public static final TextAttributesKey BRACKET_SETTING = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.BRACKET_SETTING.toString(),
                                                                                                      DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
    public static final TextAttributesKey IMPORT = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.IMPORT.toString(),
                                                                                             DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
    public static final TextAttributesKey IMPORT_ARGUMENT = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.IMPORT + "" + RobotStubTokenTypes.ARGUMENT,
                                                                                             DefaultLanguageHighlighterColors.HIGHLIGHTED_REFERENCE);
    public static final TextAttributesKey KEYWORD_DEFINITION = TextAttributesKey.createTextAttributesKey(RobotStubTokenTypes.KEYWORD_DEFINITION.toString(),
                                                                                                         DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.KEYWORD.toString(),
                                                                                              DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey PARAMETER = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.PARAMETER.toString(),
                                                                                                DefaultLanguageHighlighterColors.PARAMETER);
    public static final TextAttributesKey ARGUMENT = TextAttributesKey.createTextAttributesKey(RobotStubTokenTypes.ARGUMENT.toString(),
                                                                                               DefaultLanguageHighlighterColors.STATIC_FIELD);
    public static final TextAttributesKey VARIABLE_DEFINITION = TextAttributesKey.createTextAttributesKey(RobotStubTokenTypes.VARIABLE_DEFINITION.toString(),
                                                                                                          DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey(RobotStubTokenTypes.VARIABLE.toString(),
                                                                                               DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP);
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.COMMENT.toString(),
                                                                                              DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey GHERKIN = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.GHERKIN.toString(),
                                                                                              DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey SYNTAX_MARKER = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.SYNTAX_MARKER.toString(),
                                                                                                    DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey ERROR = TextAttributesKey.createTextAttributesKey(RobotTokenTypes.ERROR.toString(),
                                                                                            DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new RobotLexer();
    }

    static {
        keys1.put(RobotTokenTypes.HEADING, HEADING);
        keys1.put(RobotTokenTypes.COMMENT, COMMENT);
        keys1.put(RobotTokenTypes.PARAMETER, PARAMETER);
        keys1.put(RobotStubTokenTypes.ARGUMENT, ARGUMENT);
        keys1.put(RobotTokenTypes.ERROR, ERROR);
        keys1.put(RobotTokenTypes.GHERKIN, GHERKIN);
        keys1.put(RobotTokenTypes.SYNTAX_MARKER, SYNTAX_MARKER);
        keys1.put(RobotStubTokenTypes.VARIABLE, VARIABLE);
        keys1.put(RobotStubTokenTypes.VARIABLE_DEFINITION, VARIABLE_DEFINITION);
        keys1.put(RobotTokenTypes.KEYWORD, KEYWORD);
        keys1.put(RobotStubTokenTypes.KEYWORD_DEFINITION, KEYWORD_DEFINITION);
        keys1.put(RobotTokenTypes.BRACKET_SETTING, BRACKET_SETTING);
        keys1.put(RobotTokenTypes.SETTING, SETTING);
        keys1.put(RobotTokenTypes.IMPORT, IMPORT);
    }

    @NotNull
    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return pack(keys1.get(tokenType));
    }
}
