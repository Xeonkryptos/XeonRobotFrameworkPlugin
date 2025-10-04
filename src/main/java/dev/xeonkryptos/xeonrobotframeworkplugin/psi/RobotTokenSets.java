package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;

class RobotTokenSets {

    static final TokenSet WHITESPACE_SET = TokenSet.create(TokenType.WHITE_SPACE);
    static final TokenSet COMMENTS_SET = TokenSet.create(RobotTypes.COMMENT);
    static final TokenSet STRING_SET = TokenSet.create(RobotTypes.GIVEN,
                                                       RobotTypes.WHEN,
                                                       RobotTypes.THEN,
                                                       RobotTypes.AND,
                                                       RobotTypes.BUT,
                                                       RobotTypes.FOR,
                                                       RobotTypes.FOR_IN,
                                                       RobotTypes.WHILE,
                                                       RobotTypes.IF,
                                                       RobotTypes.ELSE_IF,
                                                       RobotTypes.ELSE,
                                                       RobotTypes.END,
                                                       RobotTypes.CONTINUE,
                                                       RobotTypes.BREAK,
                                                       RobotTypes.TRY,
                                                       RobotTypes.EXCEPT,
                                                       RobotTypes.FINALLY,
                                                       RobotTypes.RETURN,
                                                       RobotTypes.GROUP,
                                                       RobotTypes.WITH_NAME);
}
