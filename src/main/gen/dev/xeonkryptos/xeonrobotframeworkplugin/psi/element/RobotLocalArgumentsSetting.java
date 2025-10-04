// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotLocalArgumentsSetting extends RobotFoldable, RobotElement {

  @NotNull
  RobotLocalArgumentsSettingId getLocalArgumentsSettingId();

  @NotNull
  List<RobotLocalArgumentsSettingParameter> getLocalArgumentsSettingParameterList();

  @NotNull String getSettingName();

}
