package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PositionalArgumentImportIndex extends StringStubIndexExtension<PositionalArgument> {

    private static final StubIndexKey<String, PositionalArgument> POSITIONAL_ARGUMENT_IMPORT = StubIndexKey.createIndexKey("robot.positionalArgument.import");

    private static final PositionalArgumentImportIndex ourInstance = new PositionalArgumentImportIndex();

    public static PositionalArgumentImportIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PositionalArgument> getKey() {
        return POSITIONAL_ARGUMENT_IMPORT;
    }

    public Collection<PositionalArgument> getPositionalArgumentForImport(@NotNull String value, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        StubIndexKey<String, PositionalArgument> stubIndexKey = getKey();
        value = value.replace('/', '.').toLowerCase();
        return StubIndex.getElements(stubIndexKey, value, project, scope, PositionalArgument.class);
    }
}
