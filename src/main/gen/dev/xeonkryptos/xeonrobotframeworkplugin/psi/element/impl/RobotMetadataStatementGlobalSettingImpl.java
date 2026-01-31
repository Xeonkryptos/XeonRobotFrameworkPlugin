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
  public List<RobotParameter> getParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotParameter.class);
  }

  @Override
  @NotNull
  public PsiElement getNameElement() {
    return notNullChild(findChildByType(METADATA_KEYWORD));
  }

}
