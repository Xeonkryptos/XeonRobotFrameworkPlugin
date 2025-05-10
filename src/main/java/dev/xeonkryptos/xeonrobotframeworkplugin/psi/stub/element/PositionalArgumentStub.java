package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.psi.stubs.StubElement;

public interface PositionalArgumentStub extends StubElement<PositionalArgument> {

    String getValue();

    boolean isImportArgument();
}
