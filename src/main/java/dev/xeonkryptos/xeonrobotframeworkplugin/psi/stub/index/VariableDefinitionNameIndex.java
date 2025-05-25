package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class VariableDefinitionNameIndex extends StringStubIndexExtension<VariableDefinition> {

    public static final StubIndexKey<String, VariableDefinition> KEY = StubIndexKey.createIndexKey("robot.variableDefinition");

    private static final VariableDefinitionNameIndex ourInstance = new VariableDefinitionNameIndex();

    public static VariableDefinitionNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, VariableDefinition> getKey() {
        return KEY;
    }

    @SuppressWarnings("unused")
    public Collection<VariableDefinition> getVariableDefinitions(@NotNull String unwrappedVariableName,
                                                                 @NotNull Project project,
                                                                 @Nullable GlobalSearchScope scope) {
        StubIndexKey<String, VariableDefinition> stubIndexKey = getKey();
        String variableNameInLowerCase = unwrappedVariableName.toLowerCase();
        return StubIndex.getElements(stubIndexKey, variableNameInLowerCase, project, scope, VariableDefinition.class);
    }
}
