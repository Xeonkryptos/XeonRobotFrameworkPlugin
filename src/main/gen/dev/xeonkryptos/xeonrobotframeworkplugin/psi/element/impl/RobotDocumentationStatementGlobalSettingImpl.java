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

public class RobotDocumentationStatementGlobalSettingImpl extends RobotGlobalSettingStatementImpl implements RobotDocumentationStatementGlobalSetting {

  public RobotDocumentationStatementGlobalSettingImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitDocumentationStatementGlobalSetting(this);
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
  public PsiElement getNameIdentifier() {
    return notNullChild(findChildByType(DOCUMENTATION_KEYWORD));
  }

  @Override
  public @NotNull String getName() {
    return RobotPsiImplUtil.getName(this);
  }

}
