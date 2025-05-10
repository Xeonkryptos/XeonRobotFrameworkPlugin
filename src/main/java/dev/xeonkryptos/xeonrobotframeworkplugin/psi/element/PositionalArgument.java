package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.PositionalArgumentStub;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.StubBasedPsiElement;
import org.jetbrains.annotations.NotNull;

public interface PositionalArgument extends Argument, PsiNamedElement, StubBasedPsiElement<PositionalArgumentStub> {

    boolean isImportArgument();

    String getContent();

    @NotNull
    @Override
    PsiReference getReference();
}
