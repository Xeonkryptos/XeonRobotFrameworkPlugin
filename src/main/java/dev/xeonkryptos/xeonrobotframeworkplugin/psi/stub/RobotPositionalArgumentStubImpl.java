package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;

public class RobotPositionalArgumentStubImpl extends StubBase<RobotPositionalArgument> implements RobotPositionalArgumentStub {

    private final String myValue;
    private final boolean importArgument;

    public RobotPositionalArgumentStubImpl(final StubElement parent, final String value, final boolean importArgument) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.POSITIONAL_ARGUMENT);

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
