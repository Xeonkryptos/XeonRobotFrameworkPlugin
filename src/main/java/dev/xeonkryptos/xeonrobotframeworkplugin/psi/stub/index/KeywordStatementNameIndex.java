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

    public static final StubIndexKey<String, KeywordStatement> KEY = StubIndexKey.createIndexKey("robot.keywordStatement");

    private static final KeywordStatementNameIndex ourInstance = new KeywordStatementNameIndex();

    public static KeywordStatementNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, KeywordStatement> getKey() {
        return KEY;
    }

    public Collection<KeywordStatement> getKeywordStatements(@NotNull String keywordName, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        StubIndexKey<String, KeywordStatement> stubIndexKey = getKey();
        String keywordNameInLowerCase = keywordName.toLowerCase();
        return StubIndex.getElements(stubIndexKey, keywordNameInLowerCase, project, scope, KeywordStatement.class);
    }
}
