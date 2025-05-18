package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface KeywordInvokable extends RobotStatement, PsiNamedElement {

   @Override
   @NotNull
   String getName();

   @NotNull
   Collection<Parameter> getParameters();

   @NotNull
   Collection<PositionalArgument> getPositionalArguments();

   @NotNull
   @Override
   PsiReference getReference();
}
