// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface RobotTemplateParameter extends PsiNameIdentifierOwner, RobotStatement {

  @Nullable
  RobotTemplateParameterArgument getTemplateParameterArgument();

  @Nullable
  RobotVariable getVariable();

  @NotNull
  RobotTemplateParameterId getNameIdentifier();

}
