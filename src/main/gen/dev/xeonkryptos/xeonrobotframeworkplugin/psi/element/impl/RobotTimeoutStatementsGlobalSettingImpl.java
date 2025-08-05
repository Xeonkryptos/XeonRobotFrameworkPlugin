// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTimeoutStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.TIMEOUT_KEYWORDS;

public class RobotTimeoutStatementsGlobalSettingImpl extends RobotGlobalSettingStatementImpl implements RobotTimeoutStatementsGlobalSetting {

  public RobotTimeoutStatementsGlobalSettingImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitTimeoutStatementsGlobalSetting(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotPositionalArgument getPositionalArgument() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotPositionalArgument.class));
  }

  @Override
  @NotNull
  public PsiElement getNameElement() {
    return notNullChild(findChildByType(TIMEOUT_KEYWORDS));
  }

}
