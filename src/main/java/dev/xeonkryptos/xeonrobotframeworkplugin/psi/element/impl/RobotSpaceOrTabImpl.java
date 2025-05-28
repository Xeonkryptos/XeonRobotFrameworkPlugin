// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;

public class RobotSpaceOrTabImpl extends ASTWrapperPsiElement implements RobotSpaceOrTab {

  public RobotSpaceOrTabImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitSpaceOrTab(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getSpace() {
    return findChildByType(SPACE);
  }

  @Override
  @Nullable
  public PsiElement getTab() {
    return findChildByType(TAB);
  }

}
