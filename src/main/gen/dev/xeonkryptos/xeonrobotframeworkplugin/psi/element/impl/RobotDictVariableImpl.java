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
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
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
  public List<RobotVariableNestedAccessContent> getVariableNestedAccessContentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableNestedAccessContent.class);
  }

  @Override
  public @Nullable String getVariableName() {
    return RobotPsiUtil.getVariableName(this);
  }

  @Override
  public FoldingDescriptor[] fold(@NotNull Document ignoredDocument, boolean quick) {
    return RobotPsiUtil.fold(this, ignoredDocument, quick);
  }

  @Override
  public FoldingText getAssignedValues() {
    return RobotPsiUtil.getAssignedValues(this);
  }

}
