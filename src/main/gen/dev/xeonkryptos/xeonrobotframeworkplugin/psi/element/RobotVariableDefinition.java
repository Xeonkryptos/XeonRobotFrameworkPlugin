// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public interface RobotVariableDefinition extends PsiNameIdentifierOwner, NavigationItem, DefinedVariable, RobotQualifiedNameOwner, RobotFoldable, RobotStatement, StubBasedPsiElement<RobotVariableDefinitionStub> {

  @NotNull
  RobotVariable getVariable();

  @NotNull RobotVariable getNameIdentifier();

  @Nullable String getName();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

}
