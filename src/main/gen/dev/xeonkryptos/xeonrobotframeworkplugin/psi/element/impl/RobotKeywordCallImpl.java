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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordCallStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotKeywordCallImpl extends RobotKeywordCallExtension implements RobotKeywordCall {

  public RobotKeywordCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotKeywordCallImpl(RobotKeywordCallStub stub, IStubElementType<RobotKeywordCallStub, RobotKeywordCall> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitKeywordCall(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotKeywordCallName getKeywordCallName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotKeywordCallName.class));
  }

  @Override
  @NotNull
  public List<RobotParameter> getParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotParameter.class);
  }

  @Override
  @NotNull
  public List<RobotPositionalArgument> getPositionalArgumentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotPositionalArgument.class);
  }

  @Override
  public @NotNull String getName() {
    return RobotPsiUtil.getName(this);
  }

  @Override
  public @NotNull RobotKeywordCallName getNameIdentifier() {
    return RobotPsiUtil.getNameIdentifier(this);
  }

}
