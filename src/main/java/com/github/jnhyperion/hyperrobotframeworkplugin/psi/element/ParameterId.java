package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public interface ParameterId extends RobotStatement, PsiNamedElement {

    @NotNull
    @Override
    PsiReference getReference();
}
