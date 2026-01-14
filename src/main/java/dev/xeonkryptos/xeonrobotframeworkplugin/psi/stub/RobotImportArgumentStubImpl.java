package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;

public class RobotImportArgumentStubImpl extends StubBase<RobotImportArgument> implements RobotImportArgumentStub {

    private final String myValue;

    public RobotImportArgumentStubImpl(final StubElement parent, final String value) {
        super(parent, (IStubElementType<?, ?>) RobotTypes.IMPORT_ARGUMENT);

        myValue = value;
    }

    @Override
    public String getValue() {
        return myValue;
    }
}
