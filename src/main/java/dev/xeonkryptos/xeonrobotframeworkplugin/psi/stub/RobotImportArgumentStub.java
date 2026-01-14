package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;

public interface RobotImportArgumentStub extends StubElement<RobotImportArgument> {

    String getValue();
}
