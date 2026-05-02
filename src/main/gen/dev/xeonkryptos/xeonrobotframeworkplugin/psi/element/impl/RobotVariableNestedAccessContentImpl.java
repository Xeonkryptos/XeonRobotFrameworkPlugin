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

public class RobotVariableNestedAccessContentImpl extends RobotPsiElementBase implements RobotVariableNestedAccessContent {

  public RobotVariableNestedAccessContentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitVariableNestedAccessContent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotLiteralConstantValue> getLiteralConstantValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotLiteralConstantValue.class);
  }

  @Override
  @NotNull
  public List<RobotVariable> getVariableList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariable.class);
  }

}
