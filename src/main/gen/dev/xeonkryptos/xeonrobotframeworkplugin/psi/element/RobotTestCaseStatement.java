// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTestCaseStatementStub;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.List;

public interface RobotTestCaseStatement extends RobotQualifiedNameOwner, NavigationItem, RobotStatement, StubBasedPsiElement<RobotTestCaseStatementStub> {

  @NotNull
  List<RobotBddStatement> getBddStatementList();

  @NotNull
  List<RobotExecutableStatement> getExecutableStatementList();

  @NotNull
  List<RobotLocalSetting> getLocalSettingList();

  @NotNull
  List<RobotTemplateArguments> getTemplateArgumentsList();

  @NotNull
  RobotTestCaseId getTestCaseId();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

}
