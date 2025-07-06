// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotListVariableStub;

public interface RobotListVariable extends RobotVariable, StubBasedPsiElement<RobotListVariableStub> {

  @NotNull
  List<RobotExtendedVariableIndexAccess> getExtendedVariableIndexAccessList();

  @NotNull
  List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList();

  @NotNull
  List<RobotExtendedVariableSliceAccess> getExtendedVariableSliceAccessList();

  @Nullable
  RobotPythonExpression getPythonExpression();

  @Nullable
  RobotVariableId getVariableId();

  @Nullable PsiElement getNameIdentifier();

  @Nullable String getName();

}
