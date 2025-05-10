package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableDefinitionStub;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import org.jetbrains.annotations.NotNull;

public interface VariableDefinition extends RobotStatement, PsiNameIdentifierOwner, DefinedVariable, StubBasedPsiElement<VariableDefinitionStub> {

   boolean isNested();

   boolean isEmpty();

   @Override
   @NotNull
   String getName();

   default String getUnwrappedName() {
      if (isEmpty()) {
         return "";
      }
      String name = getName();
      return name.substring(2, name.length() - 1);
   }
}
