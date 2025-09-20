package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class VariableNameIndex extends StringStubIndexExtension<RobotVariable> {

    public static final StubIndexKey<String, RobotVariable> KEY = StubIndexKey.createIndexKey("robot.variable");

    private static final VariableNameIndex ourInstance = new VariableNameIndex();

    public static VariableNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotVariable> getKey() {
        return KEY;
    }

    public Collection<RobotVariable> getVariables(@NotNull String unwrappedVariableName, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        StubIndexKey<String, RobotVariable> stubIndexKey = getKey();
        return VariableNameUtil.INSTANCE.computeVariableNameVariants(unwrappedVariableName)
                                        .stream()
                                        .flatMap(variant -> StubIndex.getElements(stubIndexKey, variant, project, scope, RobotVariable.class).stream())
                                        .distinct()
                                        .toList();
    }
}
