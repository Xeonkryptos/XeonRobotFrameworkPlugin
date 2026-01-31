// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;
import com.intellij.psi.PsiReference;

public class RobotParameterIdImpl extends RobotPsiElementBase implements RobotParameterId {

  public RobotParameterIdImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitParameterId(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public @NotNull PsiReference getReference() {
    return RobotPsiUtil.getReference(this);
  }

}
