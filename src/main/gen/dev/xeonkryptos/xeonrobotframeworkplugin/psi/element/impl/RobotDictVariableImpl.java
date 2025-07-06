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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotDictVariableStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotDictVariableImpl extends RobotDictVariableExtension implements RobotDictVariable {

  public RobotDictVariableImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotDictVariableImpl(RobotDictVariableStub stub, IStubElementType<RobotDictVariableStub, RobotDictVariable> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitDictVariable(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotExtendedVariableKeyAccess> getExtendedVariableKeyAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExtendedVariableKeyAccess.class);
  }

  @Override
  @NotNull
  public List<RobotExtendedVariableNestedAccess> getExtendedVariableNestedAccessList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExtendedVariableNestedAccess.class);
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

}
