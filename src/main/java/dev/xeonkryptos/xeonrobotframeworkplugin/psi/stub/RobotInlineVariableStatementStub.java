package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableName;
import org.jetbrains.annotations.NotNull;

public interface RobotInlineVariableStatementStub extends NamedStub<RobotInlineVariableStatement>, VariableName {

    @NotNull
    @Override
    String getName();
}
