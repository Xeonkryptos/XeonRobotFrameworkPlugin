package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface Parameter extends Argument, PsiNameIdentifierOwner {

    String getParameterName();
}
