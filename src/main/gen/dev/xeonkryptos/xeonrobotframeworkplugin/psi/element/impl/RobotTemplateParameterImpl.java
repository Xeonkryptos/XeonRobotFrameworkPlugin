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

public class RobotTemplateParameterImpl extends RobotPsiElementBase implements RobotTemplateParameter {

  public RobotTemplateParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitTemplateParameter(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotTemplateParameterArgument getTemplateParameterArgument() {
    return PsiTreeUtil.getChildOfType(this, RobotTemplateParameterArgument.class);
  }

  @Override
  @Nullable
  public RobotVariable getVariable() {
    return PsiTreeUtil.getChildOfType(this, RobotVariable.class);
  }

  @Override
  @NotNull
  public RobotTemplateParameterId getNameIdentifier() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotTemplateParameterId.class));
  }

}
