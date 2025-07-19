// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface RobotLocalSetting extends RobotNamedElementExpression, PsiNameIdentifierOwner, RobotStatement {

  @NotNull
  List<RobotKeywordCall> getKeywordCallList();

  @NotNull
  List<RobotLocalSettingArgument> getLocalSettingArgumentList();

  @NotNull
  List<RobotParameter> getParameterList();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @NotNull
  RobotLocalSettingId getNameIdentifier();

  @NotNull String getName();

}
