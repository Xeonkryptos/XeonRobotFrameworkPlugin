package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableIdManipulator extends AbstractElementManipulator<RobotVariableId> {

    @Nullable
    @Override
    public RobotVariableId handleContentChange(@NotNull RobotVariableId variableId, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        return null;
    }
}
