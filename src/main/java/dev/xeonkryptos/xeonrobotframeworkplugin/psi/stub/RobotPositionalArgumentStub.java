package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;

public interface RobotPositionalArgumentStub extends StubElement<RobotPositionalArgument> {

    String getValue();

    boolean isImportArgument();
}
