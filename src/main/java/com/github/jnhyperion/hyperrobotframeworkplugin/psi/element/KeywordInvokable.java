package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;
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
}
