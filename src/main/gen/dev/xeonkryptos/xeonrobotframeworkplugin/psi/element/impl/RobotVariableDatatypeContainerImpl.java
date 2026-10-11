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
import com.intellij.openapi.util.TextRange;

public class RobotVariableDatatypeContainerImpl extends RobotPsiElementBase implements RobotVariableDatatypeContainer {

  public RobotVariableDatatypeContainerImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitVariableDatatypeContainer(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotVariableDatatype getVariableDatatype() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableDatatype.class);
  }

  @Override
  @Nullable
  public RobotVariableDatatypeParameterized getVariableDatatypeParameterized() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableDatatypeParameterized.class);
  }

  @Override
  @Nullable
  public RobotVariableDatatypeUnion getVariableDatatypeUnion() {
    return PsiTreeUtil.getChildOfType(this, RobotVariableDatatypeUnion.class);
  }

  @Override
  public @NotNull TextRange getInjectionRelevantTextRange() {
    return RobotPsiUtil.getInjectionRelevantTextRange(this);
  }

}
