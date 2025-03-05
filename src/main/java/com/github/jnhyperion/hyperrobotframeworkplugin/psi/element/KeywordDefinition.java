package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.KeywordDefinitionStub;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface KeywordDefinition extends RobotStatement, PsiNameIdentifierOwner, StubBasedPsiElement<KeywordDefinitionStub> {

   @NotNull
   List<KeywordInvokable> getInvokedKeywords();

   @NotNull
   Collection<DefinedVariable> getDeclaredVariables();

   String getKeywordName();
}
