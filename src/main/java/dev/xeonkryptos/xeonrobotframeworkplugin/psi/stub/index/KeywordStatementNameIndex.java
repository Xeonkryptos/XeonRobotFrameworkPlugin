package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class KeywordStatementNameIndex extends StringStubIndexExtension<RobotKeywordCall> {

    public static final StubIndexKey<String, RobotKeywordCall> KEY = StubIndexKey.createIndexKey("robot.keywordStatement");

    private static final KeywordStatementNameIndex ourInstance = new KeywordStatementNameIndex();

    public static KeywordStatementNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotKeywordCall> getKey() {
        return KEY;
    }

    public Collection<RobotKeywordCall> getKeywordCalls(@NotNull String keywordName, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        StubIndexKey<String, RobotKeywordCall> stubIndexKey = getKey();
        String keywordNameInLowerCase = keywordName.toLowerCase();
        return StubIndex.getElements(stubIndexKey, keywordNameInLowerCase, project, scope, RobotKeywordCall.class);
    }
}
