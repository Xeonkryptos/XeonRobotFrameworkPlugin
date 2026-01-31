// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;

public class RobotExecutableStatementImpl extends RobotPsiElementBase implements RobotExecutableStatement {

  public RobotExecutableStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitExecutableStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotExecutableStatement> getExecutableStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExecutableStatement.class);
  }

  @Override
  @Nullable
  public RobotKeywordCall getKeywordCall() {
    return PsiTreeUtil.getChildOfType(this, RobotKeywordCall.class);
  }

  @Override
  @Nullable
  public RobotVariableStatement getVariableStatement() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableStatement.class);
  }

}
