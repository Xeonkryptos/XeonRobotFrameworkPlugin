// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotScalarVariableStub;

public interface RobotScalarVariable extends RobotVariable, StubBasedPsiElement<RobotScalarVariableStub> {

  @Nullable
  RobotPythonExpression getPythonExpression();

  @Nullable
  RobotVariableContent getVariableContent();

  @NotNull
  List<RobotVariableIndexAccessContent> getVariableIndexAccessContentList();

  @NotNull
  List<RobotVariableNestedAccessContent> getVariableNestedAccessContentList();

  @NotNull
  List<RobotVariableSliceAccessContent> getVariableSliceAccessContentList();

}
