package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordCallStubImpl extends StubBase<RobotKeywordCall> implements RobotKeywordCallStub {

    private final String myKeyword;

    public RobotKeywordCallStubImpl(final StubElement parent, final String keyword) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.KEYWORD_CALL);

        myKeyword = keyword;
    }

    @NotNull
    @Override
    public String getName() {
        return myKeyword;
    }
}
