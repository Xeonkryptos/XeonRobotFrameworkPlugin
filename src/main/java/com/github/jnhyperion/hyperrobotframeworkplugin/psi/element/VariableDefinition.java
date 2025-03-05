package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.VariableDefinitionStub;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;

public interface VariableDefinition extends RobotStatement, PsiNameIdentifierOwner, DefinedVariable, StubBasedPsiElement<VariableDefinitionStub> {

   boolean isNested();
}
