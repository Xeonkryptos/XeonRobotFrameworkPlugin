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
import com.intellij.openapi.util.TextRange;

public class RobotPythonExpressionImpl extends RobotPsiElementBase implements RobotPythonExpression {

  public RobotPythonExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitPythonExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotPythonExpressionBody> getPythonExpressionBodyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotPythonExpressionBody.class);
  }

  @Override
  @NotNull
  public List<RobotVariable> getVariableList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariable.class);
  }

  @Override
  public @NotNull TextRange getInjectionRelevantTextRange() {
    return RobotPsiUtil.getInjectionRelevantTextRange(this);
  }

}
