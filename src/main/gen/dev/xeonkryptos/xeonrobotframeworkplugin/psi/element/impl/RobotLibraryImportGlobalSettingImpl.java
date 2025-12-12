// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotNewLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.LIBRARY_IMPORT_KEYWORD;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.WITH_NAME;

public class RobotLibraryImportGlobalSettingImpl extends RobotGlobalSettingStatementImpl implements RobotLibraryImportGlobalSetting {

  public RobotLibraryImportGlobalSettingImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitLibraryImportGlobalSetting(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotNewLibraryName getNewLibraryName() {
    return PsiTreeUtil.getChildOfType(this, RobotNewLibraryName.class);
  }

  @Override
  @NotNull
  public List<RobotParameter> getParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotParameter.class);
  }

  @Override
  @NotNull
  public List<RobotPositionalArgument> getPositionalArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotPositionalArgument.class);
  }

  @Override
  @Nullable
  public RobotPositionalArgument getImportedFile() {
    List<RobotPositionalArgument> p1 = getPositionalArgumentList();
    return p1.size() < 1 ? null : p1.get(0);
  }

  @Override
  @NotNull
  public PsiElement getNameElement() {
    return notNullChild(findChildByType(LIBRARY_IMPORT_KEYWORD));
  }

  @Override
  @Nullable
  public PsiElement getLibraryNameElement() {
    return findChildByType(WITH_NAME);
  }

}
