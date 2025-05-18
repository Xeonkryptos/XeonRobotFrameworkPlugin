package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import org.jetbrains.annotations.NotNull;

public class VariableStubImpl extends StubBase<Variable> implements VariableStub {

    private final String variableName;

    public VariableStubImpl(final StubElement parent, final String name) {
        super(parent, RobotStubTokenTypes.VARIABLE);

        variableName = name;
    }

    @NotNull
    @Override
    public String getName() {
        return variableName;
    }
}
