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

public class RobotLocalArgumentsSettingImpl extends RobotLocalArgumentsSettingExtension implements RobotLocalArgumentsSetting {

  public RobotLocalArgumentsSettingImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitLocalArgumentsSetting(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotLocalArgumentsSettingId getLocalArgumentsSettingId() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotLocalArgumentsSettingId.class));
  }

  @Override
  @NotNull
  public List<RobotLocalArgumentsSettingParameter> getLocalArgumentsSettingParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotLocalArgumentsSettingParameter.class);
  }

  @Override
  public @NotNull String getSettingName() {
    return RobotPsiImplUtil.getSettingName(this);
  }

}
