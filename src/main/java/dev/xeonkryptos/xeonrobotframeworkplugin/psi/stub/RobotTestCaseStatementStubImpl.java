package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import org.jetbrains.annotations.NotNull;

public class RobotTestCaseStatementStubImpl extends StubBase<RobotTestCaseStatement> implements RobotTestCaseStatementStub {

    private final String myKeyword;

    public RobotTestCaseStatementStubImpl(final StubElement parent, final String keyword) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.TEST_CASE_STATEMENT);

        myKeyword = keyword;
    }

    @NotNull
    @Override
    public String getName() {
        return myKeyword;
    }
}
