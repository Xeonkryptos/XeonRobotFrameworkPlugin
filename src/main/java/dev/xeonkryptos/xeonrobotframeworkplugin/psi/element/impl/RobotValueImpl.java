// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;

public class RobotValueImpl extends ASTWrapperPsiElement implements RobotValue {

  public RobotValueImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitValue(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public RobotDictVariable getDictVariable() {
    return findChildByClass(RobotDictVariable.class);
  }

  @Override
  @Nullable
  public RobotEnvironmentVariable getEnvironmentVariable() {
    return findChildByClass(RobotEnvironmentVariable.class);
  }

  @Override
  @Nullable
  public RobotListVariable getListVariable() {
    return findChildByClass(RobotListVariable.class);
  }

  @Override
  @Nullable
  public RobotLiteralValue getLiteralValue() {
    return findChildByClass(RobotLiteralValue.class);
  }

  @Override
  @Nullable
  public RobotScalarVariable getScalarVariable() {
    return findChildByClass(RobotScalarVariable.class);
  }

}
