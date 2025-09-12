// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RobotSettingsSection extends RobotSection {

  @NotNull
  List<RobotGlobalSettingStatement> getGlobalSettingStatementList();

  @NotNull
  PsiElement getNameIdentifier();

  @NotNull String getSectionName();

}
