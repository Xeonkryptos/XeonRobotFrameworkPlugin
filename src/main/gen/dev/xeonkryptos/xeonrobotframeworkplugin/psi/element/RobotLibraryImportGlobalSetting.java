// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotLibraryImportGlobalSetting extends RobotGlobalSettingStatement, RobotImportGlobalSettingExpression {

  @Nullable
  RobotNewLibraryName getNewLibraryName();

  @NotNull
  List<RobotParameter> getParameterList();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @NotNull
  RobotPositionalArgument getImportedFile();

  @NotNull
  PsiElement getNameElement();

}
