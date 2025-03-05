package com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotStubTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;

public class KeywordDefinitionStubImpl extends StubBase<KeywordDefinition> implements KeywordDefinitionStub {

    private final String myName;

    public KeywordDefinitionStubImpl(final StubElement parent, final String name) {
        super(parent, RobotStubTokenTypes.KEYWORD_DEFINITION);

        myName = name;
    }

    @Override
    public String getName() {
        return myName;
    }
}
