package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import org.jetbrains.annotations.NotNull;

public interface KeywordDefinitionStub extends NamedStub<KeywordDefinition> {

    @NotNull
    @Override
    String getName();
}
