// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.List;

public interface RobotTaskStatement extends RobotQualifiedNameOwner, PsiNameIdentifierOwner, NavigationItem, RobotStatement, StubBasedPsiElement<RobotTaskStatementStub> {

  @NotNull
  RobotTaskId getTaskId();

  @NotNull String getName();

  @NotNull RobotTaskId getNameIdentifier();

  @NotNull Icon getIcon(int flags);

  @NotNull String getQualifiedName();

  @NotNull List<RobotLocalSetting> getLocalSettings();

}
