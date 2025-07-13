package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public interface RobotReferenceElementExpression extends PsiElement {

    @NotNull
    @Override
    PsiReference getReference();
}
