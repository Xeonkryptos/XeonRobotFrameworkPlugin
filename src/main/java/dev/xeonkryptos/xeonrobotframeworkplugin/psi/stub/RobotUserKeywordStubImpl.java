package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotUserKeywordStubImpl extends StubBase<RobotUserKeywordStatement> implements RobotUserKeywordStub {

    private final String myKeyword;
    @Nullable
    private final String myEmbeddedKeywordPatternString;

    public RobotUserKeywordStubImpl(final StubElement parent, final String keyword, @Nullable final String embeddedKeywordPatternString) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.USER_KEYWORD_STATEMENT);

        myKeyword = keyword;
        myEmbeddedKeywordPatternString = embeddedKeywordPatternString;
    }

    @NotNull
    @Override
    public String getName() {
        return myKeyword;
    }

    @Nullable
    @Override
    public String getEmbeddedKeywordPatternString() {
        return myEmbeddedKeywordPatternString;
    }
}
