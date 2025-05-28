// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotStep extends PsiElement {

  @Nullable
  RobotBreakStatement getBreakStatement();

  @Nullable
  RobotContinueStatement getContinueStatement();

  @Nullable
  RobotExtendedForSyntax getExtendedForSyntax();

  @Nullable
  RobotExtendedIfSyntax getExtendedIfSyntax();

  @Nullable
  RobotExtendedTrySyntax getExtendedTrySyntax();

  @Nullable
  RobotExtendedWhileSyntax getExtendedWhileSyntax();

  @Nullable
  RobotReturnStatement getReturnStatement();

  @NotNull
  List<RobotValue> getValueList();

  @Nullable
  RobotVarStatement getVarStatement();

  @NotNull
  List<RobotWhitespace> getWhitespaceList();

}
