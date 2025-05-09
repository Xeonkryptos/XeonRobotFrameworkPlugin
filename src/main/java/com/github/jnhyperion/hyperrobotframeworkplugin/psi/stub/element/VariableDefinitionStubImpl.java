package com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotStubTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class VariableDefinitionStubImpl extends StubBase<VariableDefinition> implements VariableDefinitionStub {

    private final String myName;

    public VariableDefinitionStubImpl(final StubElement parent, final String name) {
        super(parent, RobotStubTokenTypes.VARIABLE_DEFINITION);

        myName = name;
    }

    @Override
    public @NotNull String getName() {
        return myName;
    }
}

