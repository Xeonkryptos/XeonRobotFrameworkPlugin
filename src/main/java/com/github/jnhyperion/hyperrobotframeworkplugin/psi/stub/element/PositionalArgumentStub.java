package com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.psi.stubs.StubElement;

public interface PositionalArgumentStub extends StubElement<PositionalArgument> {

    String getValue();

    boolean isImportArgument();
}
