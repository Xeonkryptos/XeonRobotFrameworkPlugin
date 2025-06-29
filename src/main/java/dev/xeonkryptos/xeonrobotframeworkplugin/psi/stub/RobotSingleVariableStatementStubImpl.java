package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import org.jetbrains.annotations.NotNull;

public class RobotSingleVariableStatementStubImpl extends StubBase<RobotSingleVariableStatement> implements RobotSingleVariableStatementStub {

    private final String myName;

    public RobotSingleVariableStatementStubImpl(final StubElement parent, final String name) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.SINGLE_VARIABLE_STATEMENT);

        myName = name;
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }
}
