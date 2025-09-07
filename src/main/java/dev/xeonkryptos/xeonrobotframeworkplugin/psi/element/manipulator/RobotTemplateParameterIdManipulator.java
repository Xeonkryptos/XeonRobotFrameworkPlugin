package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotTemplateParameterIdManipulator extends AbstractElementManipulator<RobotTemplateParameterId> {

    @Nullable
    @Override
    public RobotTemplateParameterId handleContentChange(@NotNull RobotTemplateParameterId parameterId, @NotNull TextRange textRange, String newText) throws
                                                                                                                                                     IncorrectOperationException {
        String original = parameterId.getText();
        String newContent = textRange.replace(original, newText);

        RobotTemplateParameterId newParameterId = RobotElementGenerator.getInstance(parameterId.getProject()).createNewTemplateParameterId(newContent);
        if (newParameterId == null) {
            return null;
        }
        return (RobotTemplateParameterId) parameterId.replace(newParameterId);
    }
}
