package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import com.intellij.psi.stubs.NamedStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import org.jetbrains.annotations.NotNull;

public interface KeywordStatementStub extends NamedStub<KeywordStatement> {

    @NotNull
    @Override
    String getName();
}
