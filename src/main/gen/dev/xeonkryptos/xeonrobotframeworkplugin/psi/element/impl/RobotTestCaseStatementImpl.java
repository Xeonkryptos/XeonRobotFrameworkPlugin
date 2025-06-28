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
  public List<RobotArgument> getArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotArgument.class);
  }

  @Override
  @NotNull
  public List<RobotBddStatement> getBddStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotBddStatement.class);
  }

  @Override
  @NotNull
  public List<RobotBracketSetting> getBracketSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotBracketSetting.class);
  }

  @Override
  @NotNull
  public List<RobotKeywordCall> getKeywordCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotKeywordCall.class);
  }

  @Override
  @NotNull
  public List<RobotKeywordVariableStatement> getKeywordVariableStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotKeywordVariableStatement.class);
  }

  @Override
  @NotNull
  public List<RobotParameter> getParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotParameter.class);
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

}
