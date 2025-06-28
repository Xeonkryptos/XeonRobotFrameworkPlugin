// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface RobotTestCaseStatement extends PsiNameIdentifierOwner {

  @NotNull
  List<RobotBddStatement> getBddStatementList();

  @NotNull
  List<RobotBracketSetting> getBracketSettingList();

  @NotNull
  List<RobotKeywordCall> getKeywordCallList();

  @NotNull
  List<RobotTemplateArguments> getTemplateArgumentsList();

  @NotNull
  RobotTestCaseId getTestCaseId();

  @NotNull
  List<RobotVariableStatement> getVariableStatementList();

}
