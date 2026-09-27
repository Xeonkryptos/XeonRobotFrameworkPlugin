// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.TextRange;

public interface RobotVariableDatatypeContainer extends RobotPythonInjectionExtension, RobotElement {

  @Nullable
  RobotVariableDatatype getVariableDatatype();

  @Nullable
  RobotVariableDatatypeParameterized getVariableDatatypeParameterized();

  @Nullable
  RobotVariableDatatypeUnion getVariableDatatypeUnion();

  @NotNull TextRange getInjectionRelevantTextRange();

}
