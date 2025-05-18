package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import com.intellij.psi.stubs.NamedStub;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KeywordDefinitionStub extends NamedStub<KeywordDefinition> {

    @NotNull
    @Override
    String getName();
}
