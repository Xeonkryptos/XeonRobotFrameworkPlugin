// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface RobotUserKeywordStatement extends PsiNameIdentifierOwner {

  @NotNull
  List<RobotConstantValue> getConstantValueList();

  @NotNull
  List<RobotEolBasedKeywordCall> getEolBasedKeywordCallList();

  @NotNull
  List<RobotLocalSetting> getLocalSettingList();

  @NotNull
  RobotUserKeywordStatementId getUserKeywordStatementId();

  @NotNull
  List<RobotVariable> getVariableList();

}
