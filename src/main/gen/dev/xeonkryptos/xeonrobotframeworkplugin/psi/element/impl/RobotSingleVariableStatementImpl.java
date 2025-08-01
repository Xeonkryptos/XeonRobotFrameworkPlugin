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

public class RobotSingleVariableStatementImpl extends RobotVariableStatementImpl implements RobotSingleVariableStatement {

  public RobotSingleVariableStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitSingleVariableStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotVariableDefinition getVariableDefinition() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotVariableDefinition.class));
  }

  @Override
  @NotNull
  public List<RobotVariableValue> getVariableValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableValue.class);
  }

  @Override
  public @NotNull RobotVariable getNameIdentifier() {
    return RobotPsiImplUtil.getNameIdentifier(this);
  }

}
