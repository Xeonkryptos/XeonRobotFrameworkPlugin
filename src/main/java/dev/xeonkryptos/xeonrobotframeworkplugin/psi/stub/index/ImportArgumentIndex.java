package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ImportArgumentIndex extends StringStubIndexExtension<RobotImportArgument> {

    public static final StubIndexKey<String, RobotImportArgument> KEY = StubIndexKey.createIndexKey("robot.importArgument");

    private static final ImportArgumentIndex ourInstance = new ImportArgumentIndex();

    public static ImportArgumentIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotImportArgument> getKey() {
        return KEY;
    }

    public Collection<RobotImportArgument> getImportArgument(@NotNull String value, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        StubIndexKey<String, RobotImportArgument> stubIndexKey = getKey();
        value = value.replace('/', '.').toLowerCase();
        return StubIndex.getElements(stubIndexKey, value, project, scope, RobotImportArgument.class);
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 1;
    }
}
