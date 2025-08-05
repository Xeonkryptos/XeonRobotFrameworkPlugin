// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RobotLocalSetting extends RobotStatement {

  @NotNull
  List<RobotKeywordCall> getKeywordCallList();

  @NotNull
  List<RobotLocalSettingArgument> getLocalSettingArgumentList();

  @NotNull
  RobotLocalSettingId getLocalSettingId();

  @NotNull
  List<RobotParameter> getParameterList();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @NotNull String getSettingName();

}
