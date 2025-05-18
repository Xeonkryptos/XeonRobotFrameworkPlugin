package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableStub;
import org.jetbrains.annotations.NotNull;

public interface Variable extends RobotStatement, PsiNamedElement, StubBasedPsiElement<VariableStub> {

   boolean isNested();

   boolean isEmpty();

   @NotNull
   String getName();

   @NotNull
   @Override
   PsiReference getReference();

   default String getUnwrappedName() {
      if (isEmpty()) {
         return "";
      }
      String name = getName();
      return name.substring(2, name.length() - 1);
   }
}
