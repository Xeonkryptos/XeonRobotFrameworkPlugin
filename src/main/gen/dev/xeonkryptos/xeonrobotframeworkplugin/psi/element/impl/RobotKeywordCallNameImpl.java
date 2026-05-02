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
import com.intellij.psi.PsiReference;

public class RobotKeywordCallNameImpl extends RobotPsiElementBase implements RobotKeywordCallName {

  public RobotKeywordCallNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitKeywordCallName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotKeywordCallLibrary getKeywordCallLibrary() {
    return PsiTreeUtil.getChildOfType(this, RobotKeywordCallLibrary.class);
  }

  @Override
  public @NotNull PsiReference getReference() {
    return RobotPsiUtil.getReference(this);
  }

  @Override
  @NotNull
  public PsiElement getKeywordName() {
    return notNullChild(findChildByType(KEYWORD_NAME));
  }

}
