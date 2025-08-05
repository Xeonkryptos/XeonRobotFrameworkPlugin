package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;

public interface RobotListVariableStub extends StubElement<RobotListVariable> {

    String getVariableName();
}
