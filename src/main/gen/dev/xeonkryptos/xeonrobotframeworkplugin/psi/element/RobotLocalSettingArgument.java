// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface RobotLocalSettingArgument extends PsiNameIdentifierOwner, RobotStatement {

  @NotNull
  RobotPositionalArgument getPositionalArgument();

  @NotNull
  RobotVariable getVariable();

  @Nullable RobotVariableBodyId getNameIdentifier();

}
