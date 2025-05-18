package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableDefinitionStub;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import org.jetbrains.annotations.NotNull;

public interface VariableDefinition extends RobotStatement, PsiNameIdentifierOwner, DefinedVariable, StubBasedPsiElement<VariableDefinitionStub>, VariableName {

   boolean isNested();

   @NotNull
   @Override
   String getName();
}
