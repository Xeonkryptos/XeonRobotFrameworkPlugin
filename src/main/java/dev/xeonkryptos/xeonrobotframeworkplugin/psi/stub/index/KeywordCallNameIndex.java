package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

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
        String normalizedKeywordName = KeywordUtil.normalizeKeywordName(keywordName);
        return StubIndex.getElements(stubIndexKey, normalizedKeywordName, project, scope, RobotKeywordCall.class);
    }

    public Collection<RobotKeywordCall> getKeywordCalls(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        Set<String> potentialKeywordCallNames = new LinkedHashSet<>();
        StubIndex.getInstance().processAllKeys(KEY, key -> {
            potentialKeywordCallNames.add(key);
            return true;
        }, scope);
        Set<RobotKeywordCall> locatedKeywordCalls = new LinkedHashSet<>();
        for (String potentialKeywordCallName : potentialKeywordCallNames) {
            Collection<RobotKeywordCall> keywordCalls = StubIndex.getElements(KEY, potentialKeywordCallName, project, scope, RobotKeywordCall.class);
            locatedKeywordCalls.addAll(keywordCalls);
        }
        return locatedKeywordCalls;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 3;
    }
}
