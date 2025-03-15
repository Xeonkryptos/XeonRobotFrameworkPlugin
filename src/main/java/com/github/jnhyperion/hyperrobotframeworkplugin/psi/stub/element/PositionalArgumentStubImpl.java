package com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotStubTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;

public class PositionalArgumentStubImpl extends StubBase<PositionalArgument> implements PositionalArgumentStub {

    private final String myValue;
    private final boolean importArgument;

    public PositionalArgumentStubImpl(final StubElement parent, final String value, final boolean importArgument) {
        super(parent, RobotStubTokenTypes.ARGUMENT);

        myValue = value;
        this.importArgument = importArgument;
    }

    @Override
    public String getValue() {
        return myValue;
    }

    @Override
    public boolean isImportArgument() {
        return importArgument;
    }
}
