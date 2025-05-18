package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableName;
import org.jetbrains.annotations.NotNull;

public interface VariableStub extends NamedStub<Variable>, VariableName {

    @NotNull
    @Override
    String getName();
}
