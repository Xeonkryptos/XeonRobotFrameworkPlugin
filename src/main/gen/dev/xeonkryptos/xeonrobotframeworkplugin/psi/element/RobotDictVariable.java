// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotDictVariableStub;

public interface RobotDictVariable extends RobotVariable, StubBasedPsiElement<RobotDictVariableStub> {

  @NotNull
  List<RobotExtendedVariableKeyAccess> getExtendedVariableKeyAccessList();

  @NotNull
  List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList();

  @Nullable
  RobotPythonExpression getPythonExpression();

  @Nullable
  RobotVariableContent getVariableContent();

  @Nullable RobotVariableBodyId getNameIdentifier();

  @Nullable String getName();

}
