// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;

public interface RobotTaskStatement extends PsiNameIdentifierOwner, RobotQualifiedNameOwner, NavigationItem, RobotStatement, StubBasedPsiElement<RobotTaskStatementStub> {

  @NotNull
  List<RobotBddStatement> getBddStatementList();

  @NotNull
  List<RobotExecutableStatement> getExecutableStatementList();

  @NotNull
  List<RobotLocalSetting> getLocalSettingList();

  @NotNull
  List<RobotTemplateArguments> getTemplateArgumentsList();

  @NotNull
  RobotTaskId getNameIdentifier();

  @NotNull String getName();

}
