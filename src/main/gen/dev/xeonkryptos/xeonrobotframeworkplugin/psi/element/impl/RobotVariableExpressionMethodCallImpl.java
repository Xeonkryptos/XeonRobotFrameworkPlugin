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

public class RobotVariableExpressionMethodCallImpl extends RobotPsiElementBase implements RobotVariableExpressionMethodCall {

  public RobotVariableExpressionMethodCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitVariableExpressionMethodCall(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotVariableExpressionMethodCallArgument> getVariableExpressionMethodCallArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableExpressionMethodCallArgument.class);
  }

  @Override
  @NotNull
  public RobotVariableExpressionMethodCallId getVariableExpressionMethodCallId() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotVariableExpressionMethodCallId.class));
  }

}
