// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;

public interface RobotVariableId extends PsiNamedElement, RobotReferenceElementExpression, RobotStatement {

  @NotNull
  List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList();

  @NotNull
  List<RobotVariable> getVariableList();

  @NotNull
  List<RobotVariableBodyValue> getVariableBodyValueList();

  @Nullable
  RobotVariableBodyValue getContent();

  @NotNull PsiReference getReference();

  @Nullable String getName();

}
