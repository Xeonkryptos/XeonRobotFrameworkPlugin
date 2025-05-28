// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotExtendedIfSyntax extends PsiElement {

  @NotNull
  List<RobotCommentLine> getCommentLineList();

  @NotNull
  List<RobotCondition> getConditionList();

  @NotNull
  List<RobotEmptyLine> getEmptyLineList();

  @NotNull
  List<RobotPipeStatement> getPipeStatementList();

  @NotNull
  List<RobotWhitespace> getWhitespaceList();

  @Nullable
  PsiElement getElse();

  @NotNull
  PsiElement getEnd();

  @NotNull
  PsiElement getIf();

}
