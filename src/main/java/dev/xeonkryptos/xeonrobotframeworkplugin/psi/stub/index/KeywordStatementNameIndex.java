package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class KeywordStatementNameIndex extends StringStubIndexExtension<KeywordStatement> {

    private static final StubIndexKey<String, KeywordStatement> KEYWORD_STATEMENT_NAME = StubIndexKey.createIndexKey("robot.keywordStatement");

    private static final KeywordStatementNameIndex ourInstance = new KeywordStatementNameIndex();

    public static KeywordStatementNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, KeywordStatement> getKey() {
        return KEYWORD_STATEMENT_NAME;
    }

    public Collection<KeywordStatement> getKeywordStatement(@NotNull String name, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), name.toLowerCase(), project, scope, KeywordStatement.class);
    }
}
