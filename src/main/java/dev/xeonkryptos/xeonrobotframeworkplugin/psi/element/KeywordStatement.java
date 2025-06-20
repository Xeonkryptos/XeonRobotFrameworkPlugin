package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.KeywordStatementStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface KeywordStatement extends RobotStatement, PsiNamedElement, NavigationItem, StubBasedPsiElement<KeywordStatementStub>, RobotQualifiedNameOwner {

   @NotNull
   KeywordInvokable getInvokable();

   @NotNull
   List<Argument> getArguments();

   @NotNull
   List<Parameter> getParameters();

   @NotNull
   List<PositionalArgument> getPositionalArguments();

   @NotNull
   Collection<DefinedParameter> getAvailableParameters();

   @Nullable
   DefinedVariable getGlobalVariable();

   boolean allRequiredParametersArePresent();

   void reset();

   @NotNull
   @Override
   String getName();
}
