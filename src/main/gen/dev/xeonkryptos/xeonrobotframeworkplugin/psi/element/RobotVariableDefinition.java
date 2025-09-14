// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import javax.swing.Icon;

public interface RobotVariableDefinition extends PsiNameIdentifierOwner, NavigationItem, DefinedVariable, RobotQualifiedNameOwner, RobotFoldable, RobotStatement, StubBasedPsiElement<RobotVariableDefinitionStub> {

  @NotNull
  RobotVariable getVariable();

  @NotNull RobotVariable getNameIdentifier();

  @Nullable String getName();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

}
