// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public interface RobotVariableContent extends PsiNamedElement, RobotStatement {

  @NotNull
  List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList();

  @NotNull
  List<RobotVariable> getVariableList();

  @NotNull
  List<RobotVariableBodyId> getVariableBodyIdList();

  @Nullable
  RobotVariableBodyId getContent();

  //WARNING: getName(...) is skipped
  //matching getName(RobotVariableContent, ...)
  //methods are not found in RobotPsiImplUtil

}
