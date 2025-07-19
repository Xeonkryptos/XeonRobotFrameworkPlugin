// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;

public interface RobotParameter extends PsiNameIdentifierOwner, RobotNamedElementExpression, NavigationItem, RobotArgument, RobotStatement {

  @Nullable
  RobotPositionalArgument getPositionalArgument();

  @NotNull
  RobotParameterId getNameIdentifier();

  @NotNull String getName();

}
