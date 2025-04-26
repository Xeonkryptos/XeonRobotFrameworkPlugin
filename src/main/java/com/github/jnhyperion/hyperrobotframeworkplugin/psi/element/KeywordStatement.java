package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.KeywordStatementStub;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface KeywordStatement extends RobotStatement, PsiNamedElement, StubBasedPsiElement<KeywordStatementStub> {

   @Nullable
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

   void reset();
}
