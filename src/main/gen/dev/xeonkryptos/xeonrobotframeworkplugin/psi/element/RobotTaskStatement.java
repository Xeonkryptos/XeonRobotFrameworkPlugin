// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.List;

public interface RobotTaskStatement extends RobotQualifiedNameOwner, NavigationItem, RobotStatement, StubBasedPsiElement<RobotTaskStatementStub> {

  @NotNull
  List<RobotBddStatement> getBddStatementList();

  @NotNull
  List<RobotExecutableStatement> getExecutableStatementList();

  @NotNull
  List<RobotLocalSetting> getLocalSettingList();

  @NotNull
  RobotTaskId getTaskId();

  @NotNull
  List<RobotTemplateArguments> getTemplateArgumentsList();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

}
