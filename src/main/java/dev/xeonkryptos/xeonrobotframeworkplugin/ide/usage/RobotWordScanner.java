package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLexerAdapter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;

public class RobotWordScanner extends DefaultWordsScanner {

    private static final TokenSet IDENTIFIERS = TokenSet.create(RobotStubTokenTypes.KEYWORD_DEFINITION,
                                                                RobotStubTokenTypes.KEYWORD_STATEMENT,
                                                                RobotTokenTypes.PARAMETER,
                                                                RobotStubTokenTypes.VARIABLE_DEFINITION);
    private static final TokenSet COMMENTS = TokenSet.create(RobotTokenTypes.COMMENT);
    private static final TokenSet LITERALS = TokenSet.create(RobotStubTokenTypes.ARGUMENT);

    public RobotWordScanner() {
        super(new RobotLexerAdapter(), IDENTIFIERS, COMMENTS, LITERALS);

        setMayHaveFileRefsInLiterals(true);
    }
}
