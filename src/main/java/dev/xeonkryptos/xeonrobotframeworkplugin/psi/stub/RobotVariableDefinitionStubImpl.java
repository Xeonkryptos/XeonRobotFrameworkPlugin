package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;

public class RobotVariableDefinitionStubImpl extends StubBase<RobotVariableDefinition> implements RobotVariableDefinitionStub {

    private final String myName;
    private final VariableScope myScope;
    private final VariableType variableType;

    public RobotVariableDefinitionStubImpl(final StubElement parent, final String name, final VariableScope scope, VariableType type) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.VARIABLE_DEFINITION);

        myName = name;
        myScope = scope;
        variableType = type;
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public VariableScope getScope() {
        return myScope;
    }

    @Override
    public VariableType getVariableType() {
        return variableType;
    }
}
