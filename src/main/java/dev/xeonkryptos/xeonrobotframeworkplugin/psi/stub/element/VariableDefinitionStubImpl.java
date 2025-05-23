package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class VariableDefinitionStubImpl extends StubBase<VariableDefinition> implements VariableDefinitionStub {

    private final String myName;

    public VariableDefinitionStubImpl(final StubElement parent, final String name) {
        super(parent, RobotStubTokenTypes.VARIABLE_DEFINITION);

        myName = name;
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }
}

