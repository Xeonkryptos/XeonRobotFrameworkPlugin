// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import javax.swing.Icon;

public interface RobotVariableDefinition extends PsiNamedElement, NavigationItem, DefinedVariable, RobotQualifiedNameOwner, RobotAssignedVariable, RobotElement, StubBasedPsiElement<RobotVariableDefinitionStub> {

  @Nullable
  RobotPythonExpression getPythonExpression();

  @Nullable
  RobotVariableContent getVariableContent();

  @NotNull
  List<RobotVariableIndexAccessContent> getVariableIndexAccessContentList();

  @NotNull
  List<RobotVariableNestedAccessContent> getVariableNestedAccessContentList();

  @NotNull
  List<RobotVariableSliceAccessContent> getVariableSliceAccessContentList();

  @Nullable String getName();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

}
