package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotParameterIdManipulator extends AbstractElementManipulator<RobotParameterId> {

    @Nullable
    @Override
    public RobotParameterId handleContentChange(@NotNull RobotParameterId parameterId, @NotNull TextRange textRange, String newText) throws
                                                                                                                                     IncorrectOperationException {
        String original = parameterId.getText();
        String newContent = textRange.replace(original, newText);

        RobotParameterId newParameterId = RobotElementGenerator.getInstance(parameterId.getProject()).createNewParameterId(newContent);
        if (newParameterId == null) {
            return null;
        }
        return (RobotParameterId) parameterId.replace(newParameterId);
    }
}
