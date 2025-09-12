// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RobotExecutableStatement extends RobotFoldable, RobotStatement {

  @NotNull
  List<RobotExecutableStatement> getExecutableStatementList();

  @Nullable
  RobotKeywordCall getKeywordCall();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @Nullable
  RobotVariableStatement getVariableStatement();

}
