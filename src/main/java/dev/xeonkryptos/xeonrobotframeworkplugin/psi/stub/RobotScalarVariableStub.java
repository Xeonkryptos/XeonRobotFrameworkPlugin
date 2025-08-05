package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable;

public interface RobotScalarVariableStub extends StubElement<RobotScalarVariable> {

    String getVariableName();
}
