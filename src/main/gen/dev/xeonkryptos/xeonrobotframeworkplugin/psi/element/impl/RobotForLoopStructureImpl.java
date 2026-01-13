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

public class RobotForLoopStructureImpl extends RobotForLoopStructureExtension implements RobotForLoopStructure {

  public RobotForLoopStructureImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitForLoopStructure(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotForLoopStructureParameter> getForLoopStructureParameterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotForLoopStructureParameter.class);
  }

  @Override
  @NotNull
  public List<RobotVariableDefinition> getVariableDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariableDefinition.class);
  }

  @Override
  @Nullable
  public PsiElement getForInElement() {
    return findChildByType(FOR_IN);
  }

}
