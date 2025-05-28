// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotVariablesSection extends PsiElement {

  @NotNull
  List<RobotCommentLine> getCommentLineList();

  @NotNull
  List<RobotEmptyLine> getEmptyLineList();

  @NotNull
  List<RobotVariableStatement> getVariableStatementList();

  @NotNull
  RobotVariablesHeader getVariablesHeader();

}
