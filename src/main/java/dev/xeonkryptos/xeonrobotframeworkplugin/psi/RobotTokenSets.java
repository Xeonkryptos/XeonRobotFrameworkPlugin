package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;

public class RobotTokenSets {

    public static final TokenSet WHITESPACE_SET = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS_SET = TokenSet.create(RobotTypes.COMMENT);
    public static final TokenSet STRING_SET = TokenSet.create(RobotTypes.LITERAL_CONSTANT_VALUE);
}
