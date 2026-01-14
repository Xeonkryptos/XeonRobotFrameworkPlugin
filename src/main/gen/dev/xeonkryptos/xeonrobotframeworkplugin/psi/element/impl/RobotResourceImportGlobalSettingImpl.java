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

public class RobotResourceImportGlobalSettingImpl extends RobotGlobalSettingStatementImpl implements RobotResourceImportGlobalSetting {

  public RobotResourceImportGlobalSettingImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitResourceImportGlobalSetting(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotImportArgument getImportedFile() {
    return PsiTreeUtil.getChildOfType(this, RobotImportArgument.class);
  }

  @Override
  @NotNull
  public PsiElement getNameElement() {
    return notNullChild(findChildByType(RESOURCE_IMPORT_KEYWORD));
  }

}
