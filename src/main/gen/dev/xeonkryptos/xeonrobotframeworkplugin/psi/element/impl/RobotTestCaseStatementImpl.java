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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTestCaseStatementStub;
import com.intellij.psi.stubs.IStubElementType;

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
  public List<RobotBddStatement> getBddStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotBddStatement.class);
  }

  @Override
  @NotNull
  public List<RobotExecutableStatement> getExecutableStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExecutableStatement.class);
  }

  @Override
  @NotNull
  public List<RobotLocalSetting> getLocalSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotLocalSetting.class);
  }

  @Override
  @NotNull
  public List<RobotTemplateArguments> getTemplateArgumentsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotTemplateArguments.class);
  }

  @Override
  @NotNull
  public RobotTestCaseId getNameIdentifier() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotTestCaseId.class));
  }

  @Override
  public @NotNull String getName() {
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
