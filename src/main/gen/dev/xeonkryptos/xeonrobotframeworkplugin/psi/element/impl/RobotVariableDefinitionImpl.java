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
import javax.swing.Icon;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotVariableDefinitionImpl extends RobotVariableDefinitionExtension implements RobotVariableDefinition {

  public RobotVariableDefinitionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotVariableDefinitionImpl(RobotVariableDefinitionStub stub, IStubElementType<RobotVariableDefinitionStub, RobotVariableDefinition> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitVariableDefinition(this);
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
  public @NotNull RobotVariable getNameIdentifier() {
    return RobotPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public @Nullable String getName() {
    return RobotPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull Icon getIcon(int flags) {
    return RobotPsiImplUtil.getIcon(this, flags);
  }

  @Override
  public @NotNull String getQualifiedName() {
    return RobotPsiImplUtil.getQualifiedName(this);
  }

}
