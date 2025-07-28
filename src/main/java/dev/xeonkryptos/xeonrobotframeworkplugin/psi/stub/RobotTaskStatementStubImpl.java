package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import org.jetbrains.annotations.NotNull;

public class RobotTaskStatementStubImpl extends StubBase<RobotTaskStatement> implements RobotTaskStatementStub {

    private final String myKeyword;

    public RobotTaskStatementStubImpl(final StubElement parent, final String keyword) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.TASK_STATEMENT);

        myKeyword = keyword;
    }

    @NotNull
    @Override
    public String getName() {
        return myKeyword;
    }
}
