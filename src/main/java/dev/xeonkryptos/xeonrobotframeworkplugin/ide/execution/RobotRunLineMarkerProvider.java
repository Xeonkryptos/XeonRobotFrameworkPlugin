package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution;

import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons.RunConfigurations.TestState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class RobotRunLineMarkerProvider extends RunLineMarkerContributor implements DumbAware {

    private static final Set<IElementType> EXECUTABLE_ELEMENT_TYPES = Set.of(RobotTypes.TEST_CASE_NAME,
                                                                             RobotTypes.TASK_NAME,
                                                                             RobotTypes.TEST_CASES_HEADER,
                                                                             RobotTypes.TASKS_HEADER);

    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement element) {
        IElementType type = element.getNode().getElementType();
        FileType fileType = element.getContainingFile().getFileType();
        if (EXECUTABLE_ELEMENT_TYPES.contains(type) && fileType != RobotResourceFileType.getInstance()) {
            AnAction[] actions = ExecutorAction.getActions();
            return new Info(TestState.Green2, actions);
        }
        return null;
    }
}
