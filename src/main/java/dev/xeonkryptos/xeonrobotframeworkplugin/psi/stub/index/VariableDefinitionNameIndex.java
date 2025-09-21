package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubFileElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

    public Collection<RobotVariableDefinition> getVariableDefinitions(@NotNull String unwrappedVariableName,
                                                                      @NotNull Project project,
                                                                      @Nullable GlobalSearchScope scope) {
        StubIndexKey<String, RobotVariableDefinition> stubIndexKey = getKey();
        return VariableNameUtil.INSTANCE.computeVariableNameVariants(unwrappedVariableName)
                                        .stream()
                                        .flatMap(variant -> StubIndex.getElements(stubIndexKey, variant, project, scope, RobotVariableDefinition.class)
                                                                     .stream())
                                        .distinct()
                                        .toList();
    }

    @Override
    public int getVersion() {
        return RobotStubFileElementType.STUB_FILE_VERSION + super.getVersion() + 1;
    }
}
