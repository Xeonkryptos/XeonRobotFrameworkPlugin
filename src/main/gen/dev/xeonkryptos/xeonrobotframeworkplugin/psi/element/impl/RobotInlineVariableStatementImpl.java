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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotInlineVariableStatementStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotInlineVariableStatementImpl extends RobotInlineVariableStatementExtension implements RobotInlineVariableStatement {

  public RobotInlineVariableStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotInlineVariableStatementImpl(RobotInlineVariableStatementStub stub, IStubElementType<RobotInlineVariableStatementStub, RobotInlineVariableStatement> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitInlineVariableStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotVariable getVariable() {
    return PsiTreeUtil.getChildOfType(this, RobotVariable.class);
  }

  @Override
  @NotNull
  public List<RobotVariableValue> getVariableValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableValue.class);
  }

}
