package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubFileElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class KeywordCallNameIndex extends StringStubIndexExtension<RobotKeywordCall> {

    public static final StubIndexKey<String, RobotKeywordCall> KEY = StubIndexKey.createIndexKey("robot.keywordCall");

    private static final KeywordCallNameIndex ourInstance = new KeywordCallNameIndex();

    public static KeywordCallNameIndex getInstance() {
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

    @Override
    public int getVersion() {
        return RobotStubFileElementType.STUB_FILE_VERSION + super.getVersion() + 1;
    }
}
