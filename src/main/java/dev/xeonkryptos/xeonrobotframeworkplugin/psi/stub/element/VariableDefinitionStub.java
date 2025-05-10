package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.psi.stubs.NamedStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface VariableDefinitionStub extends NamedStub<VariableDefinition> {
    
    @Override
    @NonNls
    @NotNull
    String getName();
}
