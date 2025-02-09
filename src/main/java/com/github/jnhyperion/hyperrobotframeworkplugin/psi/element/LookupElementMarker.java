package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

public interface LookupElementMarker {

    String getLookup();

    PsiElement reference();
}
