package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.tree.TokenSet;

public class RobotTokenSets {

    static final TokenSet WHITESPACE_SET = TokenSet.create(RobotTokenTypes.WHITESPACE);
    static final TokenSet COMMENTS_SET = TokenSet.create(RobotTokenTypes.COMMENT);
    static final TokenSet STRING_SET = TokenSet.create(RobotTokenTypes.GHERKIN, RobotTokenTypes.SYNTAX_MARKER);
}
