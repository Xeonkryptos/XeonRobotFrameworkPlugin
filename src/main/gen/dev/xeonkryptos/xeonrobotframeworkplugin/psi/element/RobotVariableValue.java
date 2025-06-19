// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotVariableValue extends PsiElement {

  @NotNull
  List<RobotConstantValue> getConstantValueList();

  @NotNull
  List<RobotVariable> getVariableList();

}
