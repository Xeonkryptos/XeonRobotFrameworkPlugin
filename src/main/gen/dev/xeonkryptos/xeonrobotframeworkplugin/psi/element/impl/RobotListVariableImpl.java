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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotListVariableStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotListVariableImpl extends RobotListVariableExtension implements RobotListVariable {

  public RobotListVariableImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotListVariableImpl(RobotListVariableStub stub, IStubElementType<RobotListVariableStub, RobotListVariable> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitListVariable(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotExtendedVariableIndexAccess> getExtendedVariableIndexAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExtendedVariableIndexAccess.class);
  }

  @Override
  @NotNull
  public List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExtendedVariableNestedAccess.class);
  }

  @Override
  @NotNull
  public List<RobotExtendedVariableSliceAccess> getExtendedVariableSliceAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExtendedVariableSliceAccess.class);
  }

  @Override
  @Nullable
  public RobotPythonExpression getPythonExpression() {
    return PsiTreeUtil.getChildOfType(this, RobotPythonExpression.class);
  }

  @Override
  @Nullable
  public RobotVariableId getVariableId() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableId.class);
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    return RobotPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public @Nullable String getName() {
    return RobotPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull PsiReference getReference() {
    return RobotPsiImplUtil.getReference(this);
  }

}
