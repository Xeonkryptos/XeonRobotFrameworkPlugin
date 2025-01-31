package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

public interface DefinedKeyword {

   String getKeywordName();

   boolean hasArguments();

   boolean matches(String text);

   PsiElement reference();
}
