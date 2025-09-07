// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordCallStub;

public interface RobotKeywordCall extends RobotQualifiedNameOwner, RobotKeywordCallExpression, PsiNameIdentifierOwner, NavigationItem, RobotCallArgumentsContainer, RobotStatement, StubBasedPsiElement<RobotKeywordCallStub> {

  @NotNull
  RobotKeywordCallName getKeywordCallName();

  @NotNull
  List<RobotParameter> getParameterList();

  @NotNull
  List<RobotPositionalArgument> getPositionalArgumentList();

  @NotNull String getName();

  @NotNull RobotKeywordCallName getNameIdentifier();

}
