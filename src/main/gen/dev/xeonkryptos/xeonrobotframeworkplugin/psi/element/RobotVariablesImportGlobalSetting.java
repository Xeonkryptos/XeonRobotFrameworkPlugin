// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RobotVariablesImportGlobalSetting extends RobotGlobalSettingStatement, RobotImportGlobalSettingExpression {

  @NotNull
  List<RobotParameter> getParameterList();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @NotNull
  RobotPositionalArgument getImportedFile();

  @NotNull
  PsiElement getNameElement();

}
