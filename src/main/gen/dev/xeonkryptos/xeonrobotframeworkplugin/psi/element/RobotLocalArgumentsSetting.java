// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RobotLocalArgumentsSetting extends RobotFoldable, RobotStatement {

  @NotNull
  List<RobotLocalArgumentsSettingArgument> getLocalArgumentsSettingArgumentList();

  @NotNull
  RobotLocalArgumentsSettingId getLocalArgumentsSettingId();

  @NotNull
  List<RobotVariableDefinition> getVariableDefinitionList();

  @NotNull String getSettingName();

}
