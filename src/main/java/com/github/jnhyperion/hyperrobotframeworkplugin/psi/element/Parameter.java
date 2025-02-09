package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;

public interface Parameter extends RobotStatement, PsiNamedElement {

    String getParameterName();
}
