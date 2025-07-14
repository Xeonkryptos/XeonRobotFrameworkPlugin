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

    private static final TokenSet IDENTIFIERS = TokenSet.create(RobotTypes.USER_KEYWORD_NAME,
                                                                RobotTypes.KEYWORD_NAME,
                                                                RobotTypes.PARAMETER_NAME,
                                                                RobotTypes.TEMPLATE_PARAMETER_NAME);
    private static final TokenSet COMMENTS = TokenSet.create(RobotTypes.COMMENT);
    private static final TokenSet LITERALS = TokenSet.create(RobotTypes.TASK_NAME, RobotTypes.TEST_CASE_NAME, RobotTypes.LITERAL_CONSTANT);

    @Override
    public void processWords(@NotNull CharSequence fileText, @NotNull Processor<? super WordOccurrence> processor) {
        DefaultWordsScanner wordsScanner = new DefaultWordsScanner(new RobotLexerAdapter(), IDENTIFIERS, COMMENTS, LITERALS);
        wordsScanner.setMayHaveFileRefsInLiterals(true);
        wordsScanner.processWords(fileText, processor);
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 1;
    }
}
