// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotExceptStructure extends RobotExecutableStatement, RobotFoldable {

  @NotNull
  List<RobotParameter> getParameterList();

  @Nullable
  RobotPositionalArgument getPositionalArgument();

}
