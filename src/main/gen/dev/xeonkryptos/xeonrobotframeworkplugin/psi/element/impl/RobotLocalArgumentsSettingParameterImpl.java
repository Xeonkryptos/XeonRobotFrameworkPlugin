// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;

public class RobotLocalArgumentsSettingParameterImpl extends RobotPsiElementBase implements RobotLocalArgumentsSettingParameter {

  public RobotLocalArgumentsSettingParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitLocalArgumentsSettingParameter(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotLocalArgumentsSettingParameterMandatory getLocalArgumentsSettingParameterMandatory() {
    return PsiTreeUtil.getChildOfType(this, RobotLocalArgumentsSettingParameterMandatory.class);
  }

  @Override
  @Nullable
  public RobotLocalArgumentsSettingParameterOptional getLocalArgumentsSettingParameterOptional() {
    return PsiTreeUtil.getChildOfType(this, RobotLocalArgumentsSettingParameterOptional.class);
  }

}
