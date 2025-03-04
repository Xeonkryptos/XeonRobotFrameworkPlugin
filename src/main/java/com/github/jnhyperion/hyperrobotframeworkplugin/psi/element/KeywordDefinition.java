package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface KeywordDefinition extends RobotStatement, PsiNameIdentifierOwner {

   @NotNull
   List<KeywordInvokable> getInvokedKeywords();

   @NotNull
   Collection<DefinedVariable> getDeclaredVariables();

   String getKeywordName();
}
