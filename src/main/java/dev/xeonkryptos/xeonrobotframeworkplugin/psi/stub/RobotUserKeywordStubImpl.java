package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;

public class RobotUserKeywordStubImpl extends StubBase<RobotUserKeywordStatement> implements RobotUserKeywordStub {

    private final String myKeyword;

    public RobotUserKeywordStubImpl(final StubElement parent, final String keyword) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.USER_KEYWORD_STATEMENT);

        myKeyword = keyword;
    }

    @NotNull
    @Override
    public String getName() {
        return myKeyword;
    }
}
