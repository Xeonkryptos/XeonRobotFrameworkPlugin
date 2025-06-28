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
  public List<RobotArgument> getArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotArgument.class);
  }

  @Override
  @NotNull
  public List<RobotEolFreeKeywordCall> getEolFreeKeywordCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotEolFreeKeywordCall.class);
  }

  @Override
  @NotNull
  public RobotLocalSettingId getLocalSettingId() {
    return findNotNullChildByClass(RobotLocalSettingId.class);
  }

  @Override
  @NotNull
  public List<RobotParameter> getParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotParameter.class);
  }

}
