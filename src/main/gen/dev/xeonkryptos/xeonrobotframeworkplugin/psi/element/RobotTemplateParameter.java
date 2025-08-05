// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RobotTemplateParameter extends RobotStatement {

  @Nullable
  RobotTemplateParameterArgument getTemplateParameterArgument();

  @NotNull
  RobotTemplateParameterId getTemplateParameterId();

  @Nullable
  RobotVariable getVariable();

}
