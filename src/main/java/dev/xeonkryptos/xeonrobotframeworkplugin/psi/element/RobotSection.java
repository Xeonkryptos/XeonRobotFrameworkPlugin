// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotSection extends PsiElement {

  @Nullable
  RobotCommentsSection getCommentsSection();

  @Nullable
  RobotKeywordsSection getKeywordsSection();

  @Nullable
  RobotSettingsSection getSettingsSection();

  @Nullable
  RobotTasksSection getTasksSection();

  @Nullable
  RobotTestCasesSection getTestCasesSection();

  @Nullable
  RobotVariablesSection getVariablesSection();

}
