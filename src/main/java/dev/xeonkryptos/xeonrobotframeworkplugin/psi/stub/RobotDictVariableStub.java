package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;

public interface RobotDictVariableStub extends StubElement<RobotDictVariable> {

    String getVariableName();
}
