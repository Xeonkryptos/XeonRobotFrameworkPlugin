// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTestCaseStatementStub;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.List;

public class RobotTestCaseStatementImpl extends RobotTestCaseExtension implements RobotTestCaseStatement {

  public RobotTestCaseStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotTestCaseStatementImpl(RobotTestCaseStatementStub stub, IStubElementType<RobotTestCaseStatementStub, RobotTestCaseStatement> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitTestCaseStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotTestCaseId getTestCaseId() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotTestCaseId.class));
  }

  @Override
  public @NotNull String getName() {
    return RobotPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull RobotTestCaseId getNameIdentifier() {
    return RobotPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public @NotNull Icon getIcon(int flags) {
    return RobotPsiImplUtil.getIcon(this, flags);
  }

  @Override
  public @NotNull String getQualifiedName() {
    return RobotPsiImplUtil.getQualifiedName(this);
  }

  @Override
  public @NotNull List<RobotLocalSetting> getLocalSettings() {
    return RobotPsiImplUtil.getLocalSettings(this);
  }

}
