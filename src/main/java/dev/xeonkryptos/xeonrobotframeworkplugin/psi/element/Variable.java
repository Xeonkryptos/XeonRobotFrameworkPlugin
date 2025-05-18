package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableStub;
import org.jetbrains.annotations.NotNull;

public interface Variable extends RobotStatement, PsiNamedElement, StubBasedPsiElement<VariableStub>, VariableName {

   boolean isNested();

   @NotNull
   String getName();

   @NotNull
   @Override
   PsiReference getReference();
}
