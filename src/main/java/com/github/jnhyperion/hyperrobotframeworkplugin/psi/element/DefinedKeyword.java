package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

import java.util.Collection;

public interface DefinedKeyword {

   String getKeywordName();

   boolean hasParameters();

   Collection<DefinedParameter> getParameters();

   boolean matches(String text);

   PsiElement reference();
}
