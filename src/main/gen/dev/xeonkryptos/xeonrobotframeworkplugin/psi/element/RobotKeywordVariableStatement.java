// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordVariableStatementStub;

public interface RobotKeywordVariableStatement extends PsiElement, StubBasedPsiElement<RobotKeywordVariableStatementStub> {

  @NotNull
  RobotKeywordCall getKeywordCall();

  @NotNull
  List<RobotVariable> getVariableList();

}
