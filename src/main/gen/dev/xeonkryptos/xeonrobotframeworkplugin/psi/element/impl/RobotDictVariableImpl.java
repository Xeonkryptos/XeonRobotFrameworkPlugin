// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExtendedVariableKeyAccess;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExtendedVariableNestedAccess;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotDictVariableStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
  public RobotVariableContent getVariableContent() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableContent.class);
  }

}
