// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotInlineVariableStatementStub;

public interface RobotInlineVariableStatement extends PsiNameIdentifierOwner, StubBasedPsiElement<RobotInlineVariableStatementStub> {

  @Nullable
  RobotVariable getVariable();

  @NotNull
  List<RobotVariableValue> getVariableValueList();

}
