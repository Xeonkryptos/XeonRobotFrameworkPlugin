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
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotImportArgumentStub;
import com.intellij.psi.stubs.IStubElementType;

public class RobotImportArgumentImpl extends RobotImportArgumentExtension implements RobotImportArgument {

  public RobotImportArgumentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public RobotImportArgumentImpl(RobotImportArgumentStub stub, IStubElementType<RobotImportArgumentStub, RobotImportArgument> type) {
    super(stub, type);
  }

  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitImportArgument(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public @NotNull PsiReference getReference() {
    return RobotPsiImplUtil.getReference(this);
  }

}
