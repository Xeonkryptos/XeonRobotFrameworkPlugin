// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotTestCaseStatement extends PsiElement {

  @NotNull
  List<RobotCommentLine> getCommentLineList();

  @NotNull
  List<RobotEmptyLine> getEmptyLineList();

  @NotNull
  RobotTestCaseName getTestCaseName();

  @NotNull
  List<RobotTestCaseStep> getTestCaseStepList();

  @NotNull
  PsiElement getEol();

}
