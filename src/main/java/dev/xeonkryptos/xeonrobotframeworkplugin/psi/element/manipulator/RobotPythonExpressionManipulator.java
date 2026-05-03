package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPythonExpressionManipulator extends AbstractElementManipulator<RobotPythonExpression> {

    @Nullable
    @Override
    public RobotPythonExpression handleContentChange(@NotNull RobotPythonExpression element, @NotNull TextRange range, String newText) throws IncorrectOperationException {
        String original = element.getText();
        String newContent = range.replace(original, newText);

        RobotPythonExpression newPythonExpression = RobotElementGenerator.getInstance(element.getProject()).createNewPythonExpression(newContent);
        if (newPythonExpression == null) {
            return null;
        }
        return (RobotPythonExpression) element.replace(newPythonExpression);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull RobotPythonExpression element) {
        return element.getInjectionRelevantTextRange();
    }
}
