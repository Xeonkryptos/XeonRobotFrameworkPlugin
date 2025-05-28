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

public class RobotSectionImpl extends ASTWrapperPsiElement implements RobotSection {

  public RobotSectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotCommentsSection getCommentsSection() {
    return findChildByClass(RobotCommentsSection.class);
  }

  @Override
  @Nullable
  public RobotKeywordsSection getKeywordsSection() {
    return findChildByClass(RobotKeywordsSection.class);
  }

  @Override
  @Nullable
  public RobotSettingsSection getSettingsSection() {
    return findChildByClass(RobotSettingsSection.class);
  }

  @Override
  @Nullable
  public RobotTasksSection getTasksSection() {
    return findChildByClass(RobotTasksSection.class);
  }

  @Override
  @Nullable
  public RobotTestCasesSection getTestCasesSection() {
    return findChildByClass(RobotTestCasesSection.class);
  }

  @Override
  @Nullable
  public RobotVariablesSection getVariablesSection() {
    return findChildByClass(RobotVariablesSection.class);
  }

}
