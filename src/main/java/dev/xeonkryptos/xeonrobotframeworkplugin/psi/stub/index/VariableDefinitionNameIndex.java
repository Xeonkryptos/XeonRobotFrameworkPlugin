package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

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
}
