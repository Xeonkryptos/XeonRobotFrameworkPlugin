// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotExtendedForSyntax extends PsiElement {

  @NotNull
  List<RobotCommentLine> getCommentLineList();

  @NotNull
  List<RobotEmptyLine> getEmptyLineList();

  @NotNull
  List<RobotPipeStatement> getPipeStatementList();

  @NotNull
  List<RobotValue> getValueList();

  @NotNull
  RobotVariableDefinition getVariableDefinition();

  @NotNull
  List<RobotWhitespace> getWhitespaceList();

  @NotNull
  PsiElement getEnd();

  @NotNull
  PsiElement getFor();

  @NotNull
  PsiElement getIn();

}
