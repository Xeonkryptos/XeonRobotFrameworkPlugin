// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;

public class RobotSimpleSettingStatementImpl extends ASTWrapperPsiElement implements RobotSimpleSettingStatement {

  public RobotSimpleSettingStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitSimpleSettingStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotSettingValue getSettingValue() {
    return findNotNullChildByClass(RobotSettingValue.class);
  }

  @Override
  @NotNull
  public RobotSimpleSettingName getSimpleSettingName() {
    return findNotNullChildByClass(RobotSimpleSettingName.class);
  }

  @Override
  @NotNull
  public List<RobotWhitespace> getWhitespaceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotWhitespace.class);
  }

}
