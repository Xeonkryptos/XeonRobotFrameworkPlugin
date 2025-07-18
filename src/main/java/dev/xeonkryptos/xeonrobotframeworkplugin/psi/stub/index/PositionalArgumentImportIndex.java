package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PositionalArgumentImportIndex extends StringStubIndexExtension<RobotPositionalArgument> {

    public static final StubIndexKey<String, RobotPositionalArgument> KEY = StubIndexKey.createIndexKey("robot.positionalArgument.import");

    private static final PositionalArgumentImportIndex ourInstance = new PositionalArgumentImportIndex();

    public static PositionalArgumentImportIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotPositionalArgument> getKey() {
        return KEY;
    }

    public Collection<RobotPositionalArgument> getPositionalArgumentForImport(@NotNull String value,
                                                                              @NotNull Project project,
                                                                              @NotNull GlobalSearchScope scope) {
        StubIndexKey<String, RobotPositionalArgument> stubIndexKey = getKey();
        value = value.replace('/', '.').toLowerCase();
        return StubIndex.getElements(stubIndexKey, value, project, scope, RobotPositionalArgument.class);
    }
}
