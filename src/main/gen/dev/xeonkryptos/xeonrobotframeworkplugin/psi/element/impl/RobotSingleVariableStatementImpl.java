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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotSingleVariableStatementStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotSingleVariableStatementImpl extends RobotSingleVariableStatementExtension implements RobotSingleVariableStatement {

  public RobotSingleVariableStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotSingleVariableStatementImpl(RobotSingleVariableStatementStub stub, IStubElementType<RobotSingleVariableStatementStub, RobotSingleVariableStatement> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitSingleVariableStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotVariable getVariable() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotVariable.class));
  }

  @Override
  @NotNull
  public List<RobotVariableValue> getVariableValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableValue.class);
  }

}
