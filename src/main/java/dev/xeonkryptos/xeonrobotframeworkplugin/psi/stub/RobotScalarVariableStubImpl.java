package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable;

public class RobotScalarVariableStubImpl extends StubBase<RobotScalarVariable> implements RobotScalarVariableStub {

    private final String myName;

    public RobotScalarVariableStubImpl(final StubElement parent, final String name) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.SCALAR_VARIABLE);

        myName = name;
    }

    @Override
    public String getName() {
        return myName;
    }
}
