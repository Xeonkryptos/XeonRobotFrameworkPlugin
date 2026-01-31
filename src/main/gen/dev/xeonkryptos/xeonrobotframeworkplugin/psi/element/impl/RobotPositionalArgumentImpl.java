// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;
import com.intellij.psi.PsiReference;

public class RobotPositionalArgumentImpl extends RobotPsiElementBase implements RobotPositionalArgument {

  public RobotPositionalArgumentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitPositionalArgument(this);
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
