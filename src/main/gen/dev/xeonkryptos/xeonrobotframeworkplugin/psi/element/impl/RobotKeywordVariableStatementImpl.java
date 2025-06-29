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
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordVariableStatementStub;

public class RobotKeywordVariableStatementImpl extends RobotKeywordVariableStatementExtension implements RobotKeywordVariableStatement {

  public RobotKeywordVariableStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotKeywordVariableStatementImpl(RobotKeywordVariableStatementStub stub, IStubElementType<RobotKeywordVariableStatementStub, RobotKeywordVariableStatement> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitKeywordVariableStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotKeywordCall getKeywordCall() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, RobotKeywordCall.class));
  }

  @Override
  @NotNull
  public List<RobotVariable> getVariableList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariable.class);
  }

}
