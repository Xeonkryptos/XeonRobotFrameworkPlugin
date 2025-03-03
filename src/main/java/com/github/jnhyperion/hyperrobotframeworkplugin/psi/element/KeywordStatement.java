package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KeywordStatement extends RobotStatement {

   @Nullable
   KeywordInvokable getInvokable();

   @NotNull
   List<Argument> getArguments();

   @NotNull
   List<NamedArgument> getNamedArguments();

   @NotNull
   List<PositionalArgument> getPositionalArguments();

   @NotNull
   Collection<DefinedParameter> getAvailableParameters();

   @Nullable
   DefinedVariable getGlobalVariable();
}
