package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;

public class RobotTokenSets {

    public static final TokenSet WHITESPACE_SET = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS_SET = TokenSet.create(RobotTypes.COMMENT);
    public static final TokenSet STRING_SET = TokenSet.create(RobotTypes.LITERAL_CONSTANT_VALUE, RobotTypes.LITERAL_CONSTANT);

    public static final TokenSet GHERKIN_SET = TokenSet.create(RobotTypes.GIVEN, RobotTypes.WHEN, RobotTypes.THEN, RobotTypes.AND, RobotTypes.BUT);
    public static final TokenSet LOCAL_SETTING_NAMES_SET = TokenSet.create(RobotTypes.LOCAL_ARGUMENTS_SETTING_ID, RobotTypes.LOCAL_SETTING_ID);
    public static final TokenSet GLOBAL_SETTING_NAMES_SET = TokenSet.create(RobotTypes.RESOURCE_IMPORT_KEYWORD,
                                                                            RobotTypes.LIBRARY_IMPORT_KEYWORD,
                                                                            RobotTypes.DOCUMENTATION_KEYWORD,
                                                                            RobotTypes.TAGS_KEYWORDS,
                                                                            RobotTypes.METADATA_KEYWORD,
                                                                            RobotTypes.TEMPLATE_KEYWORDS,
                                                                            RobotTypes.TIMEOUT_KEYWORDS,
                                                                            RobotTypes.VARIABLES_IMPORT_KEYWORD,
                                                                            RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS,
                                                                            RobotTypes.SUITE_NAME_KEYWORD,
                                                                            RobotTypes.UNKNOWN_SETTING_KEYWORD);

    public static final TokenSet SUPER_SPACE_SETS = TokenSet.orSet(LOCAL_SETTING_NAMES_SET,
                                                                   GHERKIN_SET,
                                                                   GLOBAL_SETTING_NAMES_SET,
                                                                   TokenSet.create(RobotTypes.KEYWORD_NAME, RobotTypes.PARAMETER, RobotTypes.POSITIONAL_ARGUMENT));
}
