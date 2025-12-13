package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubFileElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordNameUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class KeywordDefinitionNameIndex extends StringStubIndexExtension<RobotUserKeywordStatement> {

    public static final StubIndexKey<String, RobotUserKeywordStatement> KEY = StubIndexKey.createIndexKey("robot.keywordDefinition");

    @NotNull
    @Override
    public StubIndexKey<String, RobotUserKeywordStatement> getKey() {
        return KEY;
    }

    public static Collection<RobotUserKeywordStatement> getUserKeywordStatements(@NotNull String keywordName,
                                                                                 @NotNull Project project,
                                                                                 @NotNull GlobalSearchScope scope) {
        String normalizedKeywordName = KeywordNameUtil.normalizeKeywordName(keywordName);
        return StubIndex.getElements(KEY, normalizedKeywordName, project, scope, RobotUserKeywordStatement.class);
    }

    @Override
    public int getVersion() {
        return RobotStubFileElementType.STUB_FILE_VERSION + super.getVersion() + 4;
    }
}
