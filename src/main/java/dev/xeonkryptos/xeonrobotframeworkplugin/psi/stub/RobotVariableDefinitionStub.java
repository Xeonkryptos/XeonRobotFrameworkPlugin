package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

public interface RobotVariableDefinitionStub extends NamedStub<RobotVariableDefinition> {

    @NotNull
    @Override
    String getName();
}
