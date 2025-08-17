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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotUserKeywordStatementImpl extends RobotUserKeywordExtension implements RobotUserKeywordStatement {

  public RobotUserKeywordStatementImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotUserKeywordStatementImpl(RobotUserKeywordStub stub, IStubElementType<RobotUserKeywordStub, RobotUserKeywordStatement> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitUserKeywordStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotExecutableStatement> getExecutableStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotExecutableStatement.class);
  }

  @Override
  @NotNull
  public List<RobotLocalArgumentsSetting> getLocalArgumentsSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotLocalArgumentsSetting.class);
  }

  @Override
  @NotNull
  public List<RobotLocalSetting> getLocalSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotLocalSetting.class);
  }

  @Override
  @NotNull
  public RobotUserKeywordStatementId getUserKeywordStatementId() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, RobotUserKeywordStatementId.class));
  }

  @Override
  public @NotNull RobotUserKeywordStatementId getNameIdentifier() {
    return RobotPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public @NotNull String getName() {
    return RobotPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull String getQualifiedName() {
    return RobotPsiImplUtil.getQualifiedName(this);
  }

}
