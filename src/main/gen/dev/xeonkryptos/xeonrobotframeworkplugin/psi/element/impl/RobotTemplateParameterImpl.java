// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  public RobotTemplateArgument getTemplateArgument() {
    return PsiTreeUtil.getChildOfType(this, RobotTemplateArgument.class);
  }

  @Override
  @NotNull
  public RobotTemplateParameterId getTemplateParameterId() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotTemplateParameterId.class));
  }

  @Override
  public @NotNull String getParameterName() {
    return RobotPsiImplUtil.getParameterName(this);
  }

}
