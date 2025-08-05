package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;

public class RobotDictVariableStubImpl extends StubBase<RobotDictVariable> implements RobotDictVariableStub {

    private final String myName;

    public RobotDictVariableStubImpl(final StubElement parent, final String name) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.DICT_VARIABLE);

        myName = name;
    }

    @Override
    public String getVariableName() {
        return myName;
    }
}
