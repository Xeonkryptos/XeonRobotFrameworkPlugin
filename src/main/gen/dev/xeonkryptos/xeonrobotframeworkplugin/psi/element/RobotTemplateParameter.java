// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RobotTemplateParameter extends RobotArgument, RobotStatement {

  @Nullable
  RobotTemplateArgument getTemplateArgument();

  @NotNull
  RobotTemplateParameterId getTemplateParameterId();

  @NotNull String getParameterName();

}
