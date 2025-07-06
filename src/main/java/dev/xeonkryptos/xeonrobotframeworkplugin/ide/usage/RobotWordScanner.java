package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLexerAdapter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;

public class RobotWordScanner extends DefaultWordsScanner {

    private static final TokenSet IDENTIFIERS = TokenSet.create(RobotTypes.USER_KEYWORD_STATEMENT,
                                                                RobotTypes.KEYWORD_CALL,
                                                                RobotTypes.PARAMETER,
                                                                RobotTypes.INLINE_VARIABLE_STATEMENT,
                                                                RobotTypes.KEYWORD_VARIABLE_STATEMENT,
                                                                RobotTypes.SINGLE_VARIABLE_STATEMENT);
    private static final TokenSet COMMENTS = TokenSet.create(RobotTypes.COMMENT);
    private static final TokenSet LITERALS = TokenSet.create(RobotTypes.POSITIONAL_ARGUMENT);

    public RobotWordScanner() {
        super(new RobotLexerAdapter(), IDENTIFIERS, COMMENTS, LITERALS);

        setMayHaveFileRefsInLiterals(true);
    }
}
