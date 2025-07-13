package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class KeywordDefinitionNameIndex extends StringStubIndexExtension<RobotUserKeywordStatement> {

    public static final StubIndexKey<String, RobotUserKeywordStatement> KEY = StubIndexKey.createIndexKey("robot.keywordDefinition");

    private static final KeywordDefinitionNameIndex ourInstance = new KeywordDefinitionNameIndex();

    public static KeywordDefinitionNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotUserKeywordStatement> getKey() {
        return KEY;
    }

    @SuppressWarnings("unused")
    public Collection<RobotUserKeywordStatement> getKeywordDefinitions(@NotNull String keywordName, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        StubIndexKey<String, RobotUserKeywordStatement> stubIndexKey = getKey();
        String keywordNameInLowerCase = keywordName.toLowerCase();
        return StubIndex.getElements(stubIndexKey, keywordNameInLowerCase, project, scope, RobotUserKeywordStatement.class);
    }
}
