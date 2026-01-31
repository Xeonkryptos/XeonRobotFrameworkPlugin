// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotScalarVariableStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotScalarVariableImpl extends RobotScalarVariableExtension implements RobotScalarVariable {

  public RobotScalarVariableImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotScalarVariableImpl(RobotScalarVariableStub stub, IStubElementType<RobotScalarVariableStub, RobotScalarVariable> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitScalarVariable(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotPythonExpression getPythonExpression() {
    return PsiTreeUtil.getChildOfType(this, RobotPythonExpression.class);
  }

  @Override
  @Nullable
  public RobotVariableContent getVariableContent() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableContent.class);
  }

  @Override
  @NotNull
  public List<RobotVariableIndexAccessContent> getVariableIndexAccessContentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableIndexAccessContent.class);
  }

  @Override
  @NotNull
  public List<RobotVariableNestedAccessContent> getVariableNestedAccessContentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableNestedAccessContent.class);
  }

  @Override
  @NotNull
  public List<RobotVariableSliceAccessContent> getVariableSliceAccessContentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableSliceAccessContent.class);
  }

}
