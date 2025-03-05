package com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotStubTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

public class KeywordStatementStubImpl extends StubBase<KeywordStatement> implements KeywordStatementStub {

    private final String myKeyword;

    public KeywordStatementStubImpl(final StubElement parent, final String keyword) {
        super(parent, RobotStubTokenTypes.KEYWORD_STATEMENT);

        myKeyword = keyword;
    }

    @NonNls
    @Nullable
    @Override
    public String getName() {
        return myKeyword;
    }
}
