// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface RobotVariablesImportGlobalSetting extends RobotGlobalSettingStatement, PsiNameIdentifierOwner, RobotImportGlobalSettingExpression {

  @NotNull
  List<RobotParameter> getParameterList();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @NotNull
  RobotPositionalArgument getImportedFile();

  @NotNull
  PsiElement getNameIdentifier();

  @NotNull String getName();

}
