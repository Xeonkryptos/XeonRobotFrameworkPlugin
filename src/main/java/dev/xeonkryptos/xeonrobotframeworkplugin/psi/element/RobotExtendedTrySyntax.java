// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotExtendedTrySyntax extends PsiElement {

  @NotNull
  List<RobotCommentLine> getCommentLineList();

  @NotNull
  List<RobotEmptyLine> getEmptyLineList();

  @NotNull
  List<RobotPipeStatement> getPipeStatementList();

  @NotNull
  List<RobotValue> getValueList();

  @NotNull
  List<RobotWhitespace> getWhitespaceList();

  @NotNull
  PsiElement getEnd();

  @Nullable
  PsiElement getFinally();

  @NotNull
  PsiElement getTry();

}
