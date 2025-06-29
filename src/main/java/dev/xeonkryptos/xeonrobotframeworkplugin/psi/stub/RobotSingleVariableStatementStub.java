package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableName;
import org.jetbrains.annotations.NotNull;

public interface RobotSingleVariableStatementStub extends NamedStub<RobotSingleVariableStatement>, VariableName {

    @NotNull
    @Override
    String getName();
}
