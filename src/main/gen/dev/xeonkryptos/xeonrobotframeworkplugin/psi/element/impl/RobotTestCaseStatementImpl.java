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

public class RobotTestCaseStatementImpl extends RobotTestCaseExtension implements RobotTestCaseStatement {

  public RobotTestCaseStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitTestCaseStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotBddStatement> getBddStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotBddStatement.class);
  }

  @Override
  @NotNull
  public List<RobotEolBasedKeywordCall> getEolBasedKeywordCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotEolBasedKeywordCall.class);
  }

  @Override
  @NotNull
  public List<RobotLocalSetting> getLocalSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotLocalSetting.class);
  }

  @Override
  @NotNull
  public List<RobotTemplateArguments> getTemplateArgumentsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotTemplateArguments.class);
  }

  @Override
  @NotNull
  public RobotTestCaseId getTestCaseId() {
    return findNotNullChildByClass(RobotTestCaseId.class);
  }

  @Override
  @NotNull
  public List<RobotVariableStatement> getVariableStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableStatement.class);
  }

}
