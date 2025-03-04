package com.github.jnhyperion.hyperrobotframeworkplugin.ide.usage;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotLexer;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;

public class RobotWordScanner extends DefaultWordsScanner {

    private static final TokenSet IDENTIFIERS = TokenSet.create(RobotTokenTypes.KEYWORD_DEFINITION,
                                                                RobotTokenTypes.KEYWORD_STATEMENT,
                                                                RobotTokenTypes.PARAMETER,
                                                                RobotTokenTypes.VARIABLE_DEFINITION);
    private static final TokenSet COMMENTS = TokenSet.create(RobotTokenTypes.COMMENT);
    private static final TokenSet LITERALS = TokenSet.create(RobotTokenTypes.ARGUMENT);

    public RobotWordScanner() {
        super(new RobotLexer(), IDENTIFIERS, COMMENTS, LITERALS);

        setMayHaveFileRefsInLiterals(true);
    }
}
