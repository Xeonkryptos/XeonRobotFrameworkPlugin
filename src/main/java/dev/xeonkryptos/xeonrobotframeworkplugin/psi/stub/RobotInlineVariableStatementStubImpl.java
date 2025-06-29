package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import org.jetbrains.annotations.NotNull;

public class RobotInlineVariableStatementStubImpl extends StubBase<RobotInlineVariableStatement> implements RobotInlineVariableStatementStub {

    private final String myName;

    public RobotInlineVariableStatementStubImpl(final StubElement parent, final String name) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.INLINE_VARIABLE_STATEMENT);

        myName = name;
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }
}
