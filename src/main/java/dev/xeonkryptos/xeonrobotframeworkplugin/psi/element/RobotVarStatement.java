// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotVarStatement extends PsiElement {

  @NotNull
  List<RobotValue> getValueList();

  @NotNull
  RobotVariableName getVariableName();

  @NotNull
  List<RobotWhitespace> getWhitespaceList();

  @NotNull
  PsiElement getVar();

}
