// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;

public interface RobotVariableId extends PsiNamedElement, RobotReferenceElementExpression, RobotStatement {

  @Nullable
  RobotVariable getVariable();

  @Nullable
  PsiElement getContent();

  @NotNull PsiReference getReference();

  @Nullable String getName();

}
