// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;

public interface RobotUserKeywordStatement extends RobotQualifiedNameOwner, RobotUserKeywordStatementExpression, PsiNameIdentifierOwner, NavigationItem, RobotStatement, StubBasedPsiElement<RobotUserKeywordStub> {

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
