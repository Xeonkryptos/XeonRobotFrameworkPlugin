// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RobotUserKeywordStatement extends RobotQualifiedNameOwner, RobotUserKeywordStatementExpression, PsiNameIdentifierOwner, NavigationItem, RobotScopeOwner, RobotFoldable, RobotStatement, StubBasedPsiElement<RobotUserKeywordStub> {

  @NotNull
  List<RobotExecutableStatement> getExecutableStatementList();

  @NotNull
  List<RobotLocalArgumentsSetting> getLocalArgumentsSettingList();

  @NotNull
  List<RobotLocalSetting> getLocalSettingList();

  @NotNull
  RobotUserKeywordStatementId getUserKeywordStatementId();

  @NotNull RobotUserKeywordStatementId getNameIdentifier();

  @NotNull String getName();

  @NotNull String getQualifiedName();

}
