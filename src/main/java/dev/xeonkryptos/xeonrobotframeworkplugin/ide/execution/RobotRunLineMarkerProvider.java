package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution;

import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons.RunConfigurations.TestState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotRunLineMarkerProvider extends RunLineMarkerContributor {

    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement element) {
        if (element instanceof LeafPsiElement leafPsiElement) {
            IElementType type = leafPsiElement.getElementType();
            if (RobotTypes.TEST_CASES_SECTION.equals(type) || RobotTypes.TASKS_SECTION.equals(type)) {
                AnAction[] actions = ExecutorAction.getActions();
                return new Info(TestState.Green2, actions);
            } else {
                if (RobotTypes.USER_KEYWORD_STATEMENT.equals(type)) {
                    AnAction[] actions = ExecutorAction.getActions();
                    return new Info(TestState.Run, actions);
                }
            }
        }
        return null;
    }
}
