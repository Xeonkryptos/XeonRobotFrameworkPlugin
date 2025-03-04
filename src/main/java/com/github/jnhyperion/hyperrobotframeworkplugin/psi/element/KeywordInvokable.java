package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiNamedElement;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface KeywordInvokable extends RobotStatement, PsiNamedElement {

   @NotNull
   Collection<Parameter> getParameters();

   @NotNull
   Collection<PositionalArgument> getPositionalArguments();
}
