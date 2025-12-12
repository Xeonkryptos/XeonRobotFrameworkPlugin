// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RobotLibraryImportGlobalSetting extends RobotGlobalSettingStatement, GlobalSettingStatementExpression, RobotImportGlobalSettingExpression {

  @Nullable
  RobotNewLibraryName getNewLibraryName();

  @NotNull
  List<RobotParameter> getParameterList();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @Nullable
  RobotPositionalArgument getImportedFile();

  @NotNull
  PsiElement getNameElement();

  @Nullable
  PsiElement getLibraryNameElement();

}
