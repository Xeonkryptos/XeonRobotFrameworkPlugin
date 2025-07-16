package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.psi.tree.TokenSet;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLexerAdapter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;

public class RobotWordScanner extends DefaultWordsScanner {

    private static final TokenSet IDENTIFIERS = TokenSet.create(RobotTypes.USER_KEYWORD_NAME,
                                                                RobotTypes.KEYWORD_NAME,
                                                                RobotTypes.PARAMETER_NAME,
                                                                RobotTypes.TEMPLATE_PARAMETER_NAME);
    private static final TokenSet COMMENTS = TokenSet.create(RobotTypes.COMMENT);
    private static final TokenSet LITERALS = TokenSet.create(RobotTypes.TASK_NAME, RobotTypes.TEST_CASE_NAME, RobotTypes.LITERAL_CONSTANT);

    public RobotWordScanner() {
        super(new RobotLexerAdapter(), IDENTIFIERS, COMMENTS, LITERALS);

        setMayHaveFileRefsInLiterals(true);
    }
}
