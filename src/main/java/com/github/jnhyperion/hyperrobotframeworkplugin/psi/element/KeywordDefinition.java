package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface KeywordDefinition extends RobotStatement, PsiNamedElement {

   @NotNull
   List<KeywordInvokable> getInvokedKeywords();

   @NotNull
   Collection<DefinedVariable> getDeclaredVariables();

   boolean hasInlineVariables();
}
