package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

public interface LookupElementMarker {

    String getLookup();

    PsiElement reference();
}
