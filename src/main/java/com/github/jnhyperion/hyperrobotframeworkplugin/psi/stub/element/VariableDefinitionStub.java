package com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.psi.stubs.NamedStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface VariableDefinitionStub extends NamedStub<VariableDefinition> {
    
    @Override
    @NonNls
    @NotNull
    String getName();
}
