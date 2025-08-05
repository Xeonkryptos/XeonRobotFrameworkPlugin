// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RobotParameter extends RobotArgument, RobotStatement {

  @NotNull
  RobotParameterId getParameterId();

  @Nullable
  RobotPositionalArgument getPositionalArgument();

  @NotNull String getParameterName();

}
