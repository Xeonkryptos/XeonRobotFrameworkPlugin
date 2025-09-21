package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;

public class RobotVariableDefinitionStubImpl extends StubBase<RobotVariableDefinition> implements RobotVariableDefinitionStub {

    private final String myName;
    private final VariableScope myScope;

    public RobotVariableDefinitionStubImpl(final StubElement parent, final String name, final VariableScope scope) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.VARIABLE_DEFINITION);

        myName = name;
        myScope = scope;
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
}
