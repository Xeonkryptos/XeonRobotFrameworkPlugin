package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class VariableNameIndex extends StringStubIndexExtension<Variable> {

    public static final StubIndexKey<String, Variable> KEY = StubIndexKey.createIndexKey("robot.variable");

    private static final VariableNameIndex ourInstance = new VariableNameIndex();

    public static VariableNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Variable> getKey() {
        return KEY;
    }

    public Collection<Variable> getVariables(@NotNull String unwrappedVariableName, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        StubIndexKey<String, Variable> stubIndexKey = getKey();
        String variableNameInLowerCase = unwrappedVariableName.toLowerCase();
        return StubIndex.getElements(stubIndexKey, variableNameInLowerCase, project, scope, Variable.class);
    }
}
