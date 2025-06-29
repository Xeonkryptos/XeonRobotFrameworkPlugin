package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;

import java.util.List;

public class RobotKeywordVariableStatementStubImpl extends StubBase<RobotKeywordVariableStatement> implements RobotKeywordVariableStatementStub {

    private final String[] variableNames;

    public RobotKeywordVariableStatementStubImpl(final StubElement parent, String[] variableNames) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.KEYWORD_VARIABLE_STATEMENT);

        this.variableNames = variableNames;
    }

    @Override
    public String[] getVariableNames() {
        return variableNames;
    }
}
