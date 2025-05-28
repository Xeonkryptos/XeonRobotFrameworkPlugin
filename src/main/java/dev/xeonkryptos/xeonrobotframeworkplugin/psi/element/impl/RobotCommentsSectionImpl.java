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

public class RobotCommentsSectionImpl extends ASTWrapperPsiElement implements RobotCommentsSection {

  public RobotCommentsSectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitCommentsSection(this);
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
  public RobotCommentsHeader getCommentsHeader() {
    return findNotNullChildByClass(RobotCommentsHeader.class);
  }

  @Override
  @NotNull
  public List<RobotEmptyLine> getEmptyLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotEmptyLine.class);
  }

}
