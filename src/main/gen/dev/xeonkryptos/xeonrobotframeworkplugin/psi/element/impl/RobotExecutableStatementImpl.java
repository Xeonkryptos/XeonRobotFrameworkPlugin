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
  public List<RobotConstantValue> getConstantValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotConstantValue.class);
  }

  @Override
  @Nullable
  public RobotKeywordCall getKeywordCall() {
    return findChildByClass(RobotKeywordCall.class);
  }

  @Override
  @NotNull
  public List<RobotVariable> getVariableList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariable.class);
  }

  @Override
  @Nullable
  public RobotVariableStatement getVariableStatement() {
    return findChildByClass(RobotVariableStatement.class);
  }

}
