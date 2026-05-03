// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.TextRange;

public interface RobotPythonExpression extends RobotPythonInjectionExtension, RobotElement {

  @NotNull
  List<RobotPythonExpressionBody> getPythonExpressionBodyList();

  @NotNull
  List<RobotVariable> getVariableList();

  @NotNull TextRange getInjectionRelevantTextRange();

}
