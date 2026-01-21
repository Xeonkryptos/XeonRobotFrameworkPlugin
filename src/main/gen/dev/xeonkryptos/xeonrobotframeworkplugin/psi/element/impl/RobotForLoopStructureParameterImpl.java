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

public class RobotForLoopStructureParameterImpl extends RobotPsiElementBase implements RobotForLoopStructureParameter {

  public RobotForLoopStructureParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitForLoopStructureParameter(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotForLoopStructureFillParameter getForLoopStructureFillParameter() {
    return PsiTreeUtil.getChildOfType(this, RobotForLoopStructureFillParameter.class);
  }

  @Override
  @Nullable
  public RobotForLoopStructureModeParameter getForLoopStructureModeParameter() {
    return PsiTreeUtil.getChildOfType(this, RobotForLoopStructureModeParameter.class);
  }

  @Override
  @Nullable
  public RobotForLoopStructureStartParameter getForLoopStructureStartParameter() {
    return PsiTreeUtil.getChildOfType(this, RobotForLoopStructureStartParameter.class);
  }

}
