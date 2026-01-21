package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpressionBody;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPythonExpressionBodyManipulator extends AbstractElementManipulator<RobotPythonExpressionBody> {

    @Nullable
    @Override
    public RobotPythonExpressionBody handleContentChange(@NotNull RobotPythonExpressionBody element, @NotNull TextRange range, String newText) throws IncorrectOperationException {
        String original = element.getText();
        String newContent = range.replace(original, newText);

        RobotPythonExpressionBody newPythonExpressionBody = RobotElementGenerator.getInstance(element.getProject()).createNewPythonExpressionBody(newContent);
        if (newPythonExpressionBody == null) {
            return null;
        }
        return (RobotPythonExpressionBody) element.replace(newPythonExpressionBody);
    }
}
