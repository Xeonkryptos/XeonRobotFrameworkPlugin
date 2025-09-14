// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotVariablesSection extends RobotSection {

  @NotNull
  List<RobotSingleVariableStatement> getSingleVariableStatementList();

  @NotNull
  PsiElement getNameIdentifier();

  @NotNull String getSectionName();

}
