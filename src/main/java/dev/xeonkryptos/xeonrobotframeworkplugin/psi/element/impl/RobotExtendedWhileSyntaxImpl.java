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

public class RobotExtendedWhileSyntaxImpl extends ASTWrapperPsiElement implements RobotExtendedWhileSyntax {

  public RobotExtendedWhileSyntaxImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitExtendedWhileSyntax(this);
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
  public RobotCondition getCondition() {
    return findNotNullChildByClass(RobotCondition.class);
  }

  @Override
  @NotNull
  public List<RobotEmptyLine> getEmptyLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotEmptyLine.class);
  }

  @Override
  @NotNull
  public List<RobotPipeStatement> getPipeStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotPipeStatement.class);
  }

  @Override
  @NotNull
  public List<RobotWhitespace> getWhitespaceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotWhitespace.class);
  }

  @Override
  @NotNull
  public PsiElement getEnd() {
    return findNotNullChildByType(END);
  }

  @Override
  @NotNull
  public PsiElement getWhile() {
    return findNotNullChildByType(WHILE);
  }

}
