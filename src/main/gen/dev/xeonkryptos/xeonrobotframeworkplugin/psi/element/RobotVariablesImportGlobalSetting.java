// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotVariablesImportGlobalSetting extends RobotGlobalSettingStatement, GlobalSettingStatementExpression, RobotImportGlobalSettingExpression {

  @NotNull
  List<RobotParameter> getParameterList();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @Nullable
  RobotImportArgument getImportedFile();

  @NotNull
  PsiElement getNameElement();

}
