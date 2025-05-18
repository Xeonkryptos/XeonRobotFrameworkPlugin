package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class KeywordStatementStubImpl extends StubBase<KeywordStatement> implements KeywordStatementStub {

    private final String myKeyword;

    public KeywordStatementStubImpl(final StubElement parent, final String keyword) {
        super(parent, RobotStubTokenTypes.KEYWORD_STATEMENT);

        myKeyword = keyword;
    }

    @NotNull
    @Override
    public String getName() {
        return myKeyword;
    }
}
