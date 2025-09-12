// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RobotExceptStructure extends RobotExecutableStatement, RobotFoldable {

  @NotNull
  List<RobotParameter> getParameterList();

  @Nullable
  RobotPositionalArgument getPositionalArgument();

}
