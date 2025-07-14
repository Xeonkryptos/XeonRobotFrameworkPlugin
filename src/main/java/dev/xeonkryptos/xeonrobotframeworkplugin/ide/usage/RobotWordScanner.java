package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.VersionedWordsScanner;
import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Processor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLexerAdapter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;

public class RobotWordScanner extends VersionedWordsScanner {

    private static final TokenSet IDENTIFIERS = TokenSet.create(RobotTypes.USER_KEYWORD_STATEMENT,
                                                                RobotTypes.KEYWORD_CALL,
                                                                RobotTypes.PARAMETER,
                                                                RobotTypes.INLINE_VARIABLE_STATEMENT,
                                                                RobotTypes.KEYWORD_VARIABLE_STATEMENT,
                                                                RobotTypes.SINGLE_VARIABLE_STATEMENT);
    private static final TokenSet COMMENTS = TokenSet.create(RobotTypes.COMMENT);
    private static final TokenSet LITERALS = TokenSet.create(RobotTypes.POSITIONAL_ARGUMENT);

    private volatile DefaultWordsScanner myDelegate;

    @Override
    public void processWords(@NotNull CharSequence fileText, @NotNull Processor<? super WordOccurrence> processor) {
        DefaultWordsScanner delegate = this.myDelegate;
        if (delegate == null) {
            myDelegate = delegate = new DefaultWordsScanner(new RobotLexerAdapter(), IDENTIFIERS, COMMENTS, LITERALS);
            myDelegate.setMayHaveFileRefsInLiterals(true);
        }
        delegate.processWords(fileText, processor);
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 1;
    }
}
