// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotSimpleSettingStatement extends PsiElement {

  @NotNull
  RobotSettingValue getSettingValue();

  @NotNull
  RobotSimpleSettingName getSimpleSettingName();

  @NotNull
  List<RobotWhitespace> getWhitespaceList();

}
