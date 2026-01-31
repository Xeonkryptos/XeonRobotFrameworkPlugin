// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil;

public class RobotLocalSettingImpl extends RobotLocalSettingExtension implements RobotLocalSetting {

  public RobotLocalSettingImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitLocalSetting(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotKeywordCall> getKeywordCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotKeywordCall.class);
  }

  @Override
  @NotNull
  public RobotLocalSettingId getLocalSettingId() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotLocalSettingId.class));
  }

  @Override
  @NotNull
  public List<RobotPositionalArgument> getPositionalArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotPositionalArgument.class);
  }

  @Override
  public @NotNull String getSettingName() {
    return RobotPsiUtil.getSettingName(this);
  }

}
