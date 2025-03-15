package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.PositionalArgumentStub;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;

public interface PositionalArgument extends Argument, PsiNamedElement, StubBasedPsiElement<PositionalArgumentStub> {

    boolean isImportArgument();
}
