package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableBodyIdManipulator extends AbstractElementManipulator<RobotVariableBodyId> {

    @Nullable
    @Override
    public RobotVariableBodyId handleContentChange(@NotNull RobotVariableBodyId variableBodyId, @NotNull TextRange textRange, String newText) throws
                                                                                                                                              IncorrectOperationException {
        return null;
    }
}
