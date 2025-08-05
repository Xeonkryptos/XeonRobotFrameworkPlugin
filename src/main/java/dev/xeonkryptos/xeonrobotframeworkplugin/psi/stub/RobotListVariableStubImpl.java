package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;

public class RobotListVariableStubImpl extends StubBase<RobotListVariable> implements RobotListVariableStub {

    private final String myName;

    public RobotListVariableStubImpl(final StubElement parent, final String name) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.LIST_VARIABLE);

        myName = name;
    }

    @Override
    public String getVariableName() {
        return myName;
    }
}
