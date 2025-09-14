// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotLocalArgumentsSetting extends RobotFoldable, RobotStatement {

  @NotNull
  List<RobotLocalArgumentsSettingArgument> getLocalArgumentsSettingArgumentList();

  @NotNull
  RobotLocalArgumentsSettingId getLocalArgumentsSettingId();

  @NotNull
  List<RobotVariableDefinition> getVariableDefinitionList();

  @NotNull String getSettingName();

}
