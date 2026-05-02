package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableContentManipulator extends AbstractElementManipulator<RobotVariableContent> {

    @Nullable
    @Override
    public RobotVariableContent handleContentChange(@NotNull RobotVariableContent variableContent, @NotNull TextRange textRange, String newText) throws IncorrectOperationException {
        String original = variableContent.getText();
        String newContent = textRange.replace(original, newText);

        RobotVariableContent newVariableBodyId = RobotElementGenerator.getInstance(variableContent.getProject()).createNewVariableContent(newContent);
        if (newVariableBodyId == null) {
            return null;
        }
        return (RobotVariableContent) variableContent.replace(newVariableBodyId);
    }
}
