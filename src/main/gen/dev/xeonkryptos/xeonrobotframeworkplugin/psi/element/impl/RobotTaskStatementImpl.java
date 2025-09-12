// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.List;

public class RobotTaskStatementImpl extends RobotTaskExtension implements RobotTaskStatement {

  public RobotTaskStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotTaskStatementImpl(RobotTaskStatementStub stub, IStubElementType<RobotTaskStatementStub, RobotTaskStatement> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitTaskStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public RobotTaskId getTaskId() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotTaskId.class));
  }

  @Override
  public @NotNull String getName() {
    return RobotPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull RobotTaskId getNameIdentifier() {
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
