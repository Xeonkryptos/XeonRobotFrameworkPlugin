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

public class RobotMetadataStatementGlobalSettingImpl extends RobotGlobalSettingStatementImpl implements RobotMetadataStatementGlobalSetting {

  public RobotMetadataStatementGlobalSettingImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitMetadataStatementGlobalSetting(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotPositionalArgument> getPositionalArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotPositionalArgument.class);
  }

  @Override
  @NotNull
  public PsiElement getNameElement() {
    return notNullChild(findChildByType(METADATA_KEYWORD));
  }

  @Override
  @Nullable
  public RobotPositionalArgument getMetadataName() {
    List<RobotPositionalArgument> p1 = getPositionalArgumentList();
    return p1.size() < 1 ? null : p1.get(0);
  }

  @Override
  @Nullable
  public RobotPositionalArgument getMetadataValue() {
    List<RobotPositionalArgument> p1 = getPositionalArgumentList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}
