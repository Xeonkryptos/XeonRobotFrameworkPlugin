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
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotPositionalArgumentStub;

public class RobotPositionalArgumentImpl extends RobotPositionalArgumentExtension implements RobotPositionalArgument {

  public RobotPositionalArgumentImpl(ASTNode node) {
    super(node);
  }

  public RobotPositionalArgumentImpl(RobotPositionalArgumentStub stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitPositionalArgument(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

}
