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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import com.intellij.psi.PsiReference;

public class RobotVariableIdImpl extends RobotPsiElementBase implements RobotVariableId {

  public RobotVariableIdImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitVariableId(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotVariable getVariable() {
    return PsiTreeUtil.getChildOfType(this, RobotVariable.class);
  }

  @Override
  @NotNull
  public List<RobotVariableBodyValue> getVariableBodyValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableBodyValue.class);
  }

  @Override
  @Nullable
  public RobotVariableBodyValue getContent() {
    List<RobotVariableBodyValue> p1 = getVariableBodyValueList();
    return p1.size() < 1 ? null : p1.get(0);
  }

  @Override
  public @NotNull PsiReference getReference() {
    return RobotPsiImplUtil.getReference(this);
  }

  @Override
  public @Nullable String getName() {
    return RobotPsiImplUtil.getName(this);
  }

}
