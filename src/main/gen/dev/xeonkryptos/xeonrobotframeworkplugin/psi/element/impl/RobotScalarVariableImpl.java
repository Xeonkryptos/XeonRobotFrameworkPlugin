// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;

public class RobotScalarVariableImpl extends RobotVariableImpl implements RobotScalarVariable {

  public RobotScalarVariableImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitScalarVariable(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotExtendedVariableIndexAccess> getExtendedVariableIndexAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExtendedVariableIndexAccess.class);
  }

  @Override
  @NotNull
  public List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExtendedVariableNestedAccess.class);
  }

  @Override
  @NotNull
  public List<RobotExtendedVariableSliceAccess> getExtendedVariableSliceAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExtendedVariableSliceAccess.class);
  }

  @Override
  @NotNull
  public RobotVariableId getVariableId() {
    return findNotNullChildByClass(RobotVariableId.class);
  }

}
