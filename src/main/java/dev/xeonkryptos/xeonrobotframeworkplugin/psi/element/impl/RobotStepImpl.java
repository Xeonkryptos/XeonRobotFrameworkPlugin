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

public class RobotStepImpl extends ASTWrapperPsiElement implements RobotStep {

  public RobotStepImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitStep(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotBreakStatement getBreakStatement() {
    return findChildByClass(RobotBreakStatement.class);
  }

  @Override
  @Nullable
  public RobotContinueStatement getContinueStatement() {
    return findChildByClass(RobotContinueStatement.class);
  }

  @Override
  @Nullable
  public RobotExtendedForSyntax getExtendedForSyntax() {
    return findChildByClass(RobotExtendedForSyntax.class);
  }

  @Override
  @Nullable
  public RobotExtendedIfSyntax getExtendedIfSyntax() {
    return findChildByClass(RobotExtendedIfSyntax.class);
  }

  @Override
  @Nullable
  public RobotExtendedTrySyntax getExtendedTrySyntax() {
    return findChildByClass(RobotExtendedTrySyntax.class);
  }

  @Override
  @Nullable
  public RobotExtendedWhileSyntax getExtendedWhileSyntax() {
    return findChildByClass(RobotExtendedWhileSyntax.class);
  }

  @Override
  @Nullable
  public RobotReturnStatement getReturnStatement() {
    return findChildByClass(RobotReturnStatement.class);
  }

  @Override
  @NotNull
  public List<RobotValue> getValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotValue.class);
  }

  @Override
  @Nullable
  public RobotVarStatement getVarStatement() {
    return findChildByClass(RobotVarStatement.class);
  }

  @Override
  @NotNull
  public List<RobotWhitespace> getWhitespaceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotWhitespace.class);
  }

}
