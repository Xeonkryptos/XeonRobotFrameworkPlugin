package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableBodyIdManipulator extends AbstractElementManipulator<RobotVariableBodyId> {

    @Nullable
    @Override
    public RobotVariableBodyId handleContentChange(@NotNull RobotVariableBodyId variableBodyId, @NotNull TextRange textRange, String newText) throws
                                                                                                                                              IncorrectOperationException {
        String original = variableBodyId.getText();
        String newContent = textRange.replace(original, newText);

        RobotVariableBodyId newVariableBodyId = RobotElementGenerator.getInstance(variableBodyId.getProject()).createNewVariableBodyId(newContent);
        if (newVariableBodyId == null) {
            return null;
        }
        return (RobotVariableBodyId) variableBodyId.replace(newVariableBodyId);
    }
}
