package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class EmbeddedKeywordDefinitionNameIndex extends StringStubIndexExtension<RobotUserKeywordStatement> {

    public static final StubIndexKey<String, RobotUserKeywordStatement> KEY = StubIndexKey.createIndexKey("robot.embeddedKeywordDefinition");

    @NotNull
    @Override
    public StubIndexKey<String, RobotUserKeywordStatement> getKey() {
        return KEY;
    }

    public static Collection<RobotUserKeywordStatement> getEmbeddedUserKeywordStatements(@NotNull String keywordName, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        // In contrast to "normal" keywords without embedded arguments, embedded keywords don't get spaces and underscores ignored
        String normalizedKeywordName = keywordName.toLowerCase();
        Set<String> matchingKeys = new HashSet<>();
        GlobalSearchScope unitedWithProjectScope = scope.uniteWith(GlobalSearchScope.projectScope(project));
        StubIndex.getInstance().processAllKeys(KEY, embeddedKeywordNamePatternString -> {
            if (!matchingKeys.contains(embeddedKeywordNamePatternString)) {
                Pattern pattern = Pattern.compile(embeddedKeywordNamePatternString);
                if (pattern.matcher(normalizedKeywordName).matches()) {
                    matchingKeys.add(embeddedKeywordNamePatternString);
                }
            }
            return true;
        }, unitedWithProjectScope);

        List<RobotUserKeywordStatement> statements = new ArrayList<>();
        for (String matchingKey : matchingKeys) {
            Collection<RobotUserKeywordStatement> elements = StubIndex.getElements(KEY, matchingKey, project, scope, RobotUserKeywordStatement.class);
            statements.addAll(elements);
        }
        return statements;
    }

    @Override
    public int getVersion() {
        return super.getVersion();
    }
}
