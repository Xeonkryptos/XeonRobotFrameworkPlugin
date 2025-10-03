// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTestCaseStatementStub;
import javax.swing.Icon;

public interface RobotTestCaseStatement extends RobotQualifiedNameOwner, PsiNameIdentifierOwner, NavigationItem, RobotFoldable, RobotScopeOwner, RobotStatement, StubBasedPsiElement<RobotTestCaseStatementStub> {

  @NotNull
  List<RobotLocalSetting> getLocalSettingList();

  @NotNull
  RobotTestCaseId getTestCaseId();

  @NotNull String getName();

  @NotNull RobotTestCaseId getNameIdentifier();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

  @NotNull List<RobotLocalSetting> getLocalSettings();

}
