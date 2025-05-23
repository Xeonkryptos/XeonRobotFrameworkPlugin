package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableName;
import org.jetbrains.annotations.NotNull;

public interface VariableDefinitionStub extends NamedStub<VariableDefinition>, VariableName {

    @NotNull
    @Override
    String getName();
}
