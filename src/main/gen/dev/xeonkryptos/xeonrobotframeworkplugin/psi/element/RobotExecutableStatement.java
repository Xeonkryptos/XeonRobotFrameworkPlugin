// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotExecutableStatement extends PsiElement {

  @NotNull
  List<RobotConstantValue> getConstantValueList();

  @Nullable
  RobotKeywordCall getKeywordCall();

  @NotNull
  List<RobotVariable> getVariableList();

  @Nullable
  RobotVariableStatement getVariableStatement();

}
