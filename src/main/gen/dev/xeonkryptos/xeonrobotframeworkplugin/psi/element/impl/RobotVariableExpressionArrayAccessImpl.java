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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil;

public class RobotVariableExpressionArrayAccessImpl extends RobotPsiElementBase implements RobotVariableExpressionArrayAccess {

  public RobotVariableExpressionArrayAccessImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitVariableExpressionArrayAccess(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotVariableIndexAccessContent getVariableIndexAccessContent() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableIndexAccessContent.class);
  }

  @Override
  @Nullable
  public RobotVariableNestedAccessContent getVariableNestedAccessContent() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableNestedAccessContent.class);
  }

  @Override
  @Nullable
  public RobotVariableSliceAccessContent getVariableSliceAccessContent() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableSliceAccessContent.class);
  }

}
