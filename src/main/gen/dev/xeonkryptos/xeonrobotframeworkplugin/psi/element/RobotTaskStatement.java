// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;
import javax.swing.Icon;

public interface RobotTaskStatement extends RobotQualifiedNameOwner, PsiNameIdentifierOwner, NavigationItem, RobotStatement, StubBasedPsiElement<RobotTaskStatementStub> {

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

  @NotNull String getName();

  @NotNull RobotTaskId getNameIdentifier();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

}
