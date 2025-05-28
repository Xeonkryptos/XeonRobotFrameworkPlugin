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

public class RobotKeywordsSectionImpl extends ASTWrapperPsiElement implements RobotKeywordsSection {

  public RobotKeywordsSectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitKeywordsSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotCommentLine> getCommentLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotCommentLine.class);
  }

  @Override
  @NotNull
  public List<RobotEmptyLine> getEmptyLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotEmptyLine.class);
  }

  @Override
  @NotNull
  public List<RobotKeywordStatement> getKeywordStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotKeywordStatement.class);
  }

  @Override
  @NotNull
  public RobotKeywordsHeader getKeywordsHeader() {
    return findNotNullChildByClass(RobotKeywordsHeader.class);
  }

}
