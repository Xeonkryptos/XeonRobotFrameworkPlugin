// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotVariableContent extends RobotStatement {

  @NotNull
  List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList();

  @NotNull
  List<RobotVariable> getVariableList();

  @NotNull
  List<RobotVariableBodyId> getVariableBodyIdList();

  @Nullable
  RobotVariableBodyId getContent();

}
