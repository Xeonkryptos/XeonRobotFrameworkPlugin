package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class KeywordDefinitionStubImpl extends StubBase<KeywordDefinition> implements KeywordDefinitionStub {

    private final String myName;

    public KeywordDefinitionStubImpl(final StubElement parent, final String name) {
        super(parent, RobotStubTokenTypes.KEYWORD_DEFINITION);

        myName = name;
    }

    @Override
    public @NotNull String getName() {
        return myName;
    }
}
