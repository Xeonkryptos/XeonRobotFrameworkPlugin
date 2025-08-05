// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RobotVariableContent extends RobotStatement {

  @NotNull
  List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList();

  @NotNull
  List<RobotVariable> getVariableList();

  @NotNull
  List<RobotVariableBodyId> getVariableBodyIdList();

  @Nullable
  RobotVariableBodyId getContent();

}
