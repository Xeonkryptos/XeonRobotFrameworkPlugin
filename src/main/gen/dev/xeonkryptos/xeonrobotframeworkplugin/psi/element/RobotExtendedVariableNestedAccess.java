// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotExtendedVariableNestedAccess extends RobotStatement {

  @NotNull
  List<RobotLiteralConstantValue> getLiteralConstantValueList();

  @NotNull
  List<RobotVariable> getVariableList();

  @NotNull
  List<RobotVariableBodyId> getVariableBodyIdList();

}
