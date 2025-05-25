package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class KeywordDefinitionNameIndex extends StringStubIndexExtension<KeywordDefinition> {

    public static final StubIndexKey<String, KeywordDefinition> KEY = StubIndexKey.createIndexKey("robot.keywordDefinition");

    private static final KeywordDefinitionNameIndex ourInstance = new KeywordDefinitionNameIndex();

    public static KeywordDefinitionNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, KeywordDefinition> getKey() {
        return KEY;
    }

    @SuppressWarnings("unused")
    public Collection<KeywordDefinition> getKeywordDefinitions(@NotNull String keywordName, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        StubIndexKey<String, KeywordDefinition> stubIndexKey = getKey();
        String keywordNameInLowerCase = keywordName.toLowerCase();
        return StubIndex.getElements(stubIndexKey, keywordNameInLowerCase, project, scope, KeywordDefinition.class);
    }
}
