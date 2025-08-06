// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTestCaseStatementStub;
import javax.swing.Icon;

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

  @NotNull String getName();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

}
