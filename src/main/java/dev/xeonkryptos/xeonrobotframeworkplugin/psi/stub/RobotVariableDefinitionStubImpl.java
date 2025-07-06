package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

public class RobotVariableDefinitionStubImpl extends StubBase<RobotVariableDefinition> implements RobotVariableDefinitionStub {

    private final String myName;

    public RobotVariableDefinitionStubImpl(final StubElement parent, final String name) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.VARIABLE_DEFINITION);

        myName = name;
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }
}
