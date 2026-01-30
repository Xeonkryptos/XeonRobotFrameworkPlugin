package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class VariableDefinitionNameIndex extends StringStubIndexExtension<RobotVariableDefinition> {

    public static final StubIndexKey<String, RobotVariableDefinition> KEY = StubIndexKey.createIndexKey("robot.variableDefinition");

    private static final VariableDefinitionNameIndex ourInstance = new VariableDefinitionNameIndex();

    public static VariableDefinitionNameIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RobotVariableDefinition> getKey() {
        return KEY;
    }

    public Collection<RobotVariableDefinition> getVariableDefinitions(@NotNull String variableName, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        StubIndexKey<String, RobotVariableDefinition> stubIndexKey = getKey();
        return VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName)
                                        .stream()
                                        .flatMap(variant -> StubIndex.getElements(stubIndexKey, variant, project, scope, RobotVariableDefinition.class).stream())
                                        .distinct()
                                        .toList();
    }

    public Collection<RobotVariableDefinition> getVariableDefinitions(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        Set<String> potentialVariableDefinitionNames = new LinkedHashSet<>();
        StubIndex.getInstance().processAllKeys(KEY, key -> {
            potentialVariableDefinitionNames.add(key);
            return true;
        }, scope);
        Set<RobotVariableDefinition> locatedVariableDefinitions = new LinkedHashSet<>();
        for (String potentialVariableDefinitionName : potentialVariableDefinitionNames) {
            Collection<RobotVariableDefinition> variableDefinitions = StubIndex.getElements(KEY, potentialVariableDefinitionName, project, scope, RobotVariableDefinition.class);
            locatedVariableDefinitions.addAll(variableDefinitions);
        }
        return locatedVariableDefinitions;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 3;
    }
}
