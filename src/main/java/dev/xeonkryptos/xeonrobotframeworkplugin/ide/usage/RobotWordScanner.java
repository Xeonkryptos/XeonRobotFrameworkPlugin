package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLexer;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;

public class RobotWordScanner extends DefaultWordsScanner {

    private static final TokenSet IDENTIFIERS = TokenSet.create(RobotStubTokenTypes.KEYWORD_DEFINITION,
                                                                RobotStubTokenTypes.KEYWORD_STATEMENT,
                                                                RobotTokenTypes.PARAMETER,
                                                                RobotStubTokenTypes.VARIABLE_DEFINITION);
    private static final TokenSet COMMENTS = TokenSet.create(RobotTokenTypes.COMMENT);
    private static final TokenSet LITERALS = TokenSet.create(RobotStubTokenTypes.ARGUMENT);

    public RobotWordScanner() {
        super(new RobotLexer(), IDENTIFIERS, COMMENTS, LITERALS);

        setMayHaveFileRefsInLiterals(true);
    }
}
