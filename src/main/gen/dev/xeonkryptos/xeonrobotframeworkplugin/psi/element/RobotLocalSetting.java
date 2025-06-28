// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface RobotLocalSetting extends PsiNameIdentifierOwner {

  @NotNull
  List<RobotArgument> getArgumentList();

  @NotNull
  List<RobotEolFreeKeywordCall> getEolFreeKeywordCallList();

  @NotNull
  RobotLocalSettingId getLocalSettingId();

  @NotNull
  List<RobotParameter> getParameterList();

}
