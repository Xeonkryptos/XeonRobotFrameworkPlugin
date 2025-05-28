// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotVariableStatement extends PsiElement {

  @NotNull
  RobotVariableDefinition getVariableDefinition();

  @NotNull
  List<RobotVariableValue> getVariableValueList();

  @NotNull
  List<RobotWhitespace> getWhitespaceList();

}
