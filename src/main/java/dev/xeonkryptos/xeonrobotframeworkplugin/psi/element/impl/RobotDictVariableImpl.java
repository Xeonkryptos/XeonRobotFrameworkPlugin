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

public class RobotDictVariableImpl extends ASTWrapperPsiElement implements RobotDictVariable {

  public RobotDictVariableImpl(@NotNull ASTNode node) {
    super(node);
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
  @NotNull
  public RobotVariableName getVariableName() {
    return findNotNullChildByClass(RobotVariableName.class);
  }

  @Override
  @NotNull
  public PsiElement getDictVariableStart() {
    return findNotNullChildByType(DICT_VARIABLE_START);
  }

  @Override
  @NotNull
  public PsiElement getRbrace() {
    return findNotNullChildByType(RBRACE);
  }

}
