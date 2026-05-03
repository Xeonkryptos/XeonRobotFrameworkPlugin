package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotConditionalContentManipulator extends AbstractElementManipulator<RobotConditionalContent> {

    @Nullable
    @Override
    public RobotConditionalContent handleContentChange(@NotNull RobotConditionalContent element, @NotNull TextRange range, String newText) throws IncorrectOperationException {
        String original = element.getText();
        String newContent = range.replace(original, newText);

        RobotConditionalContent newConditionalContent = RobotElementGenerator.getInstance(element.getProject()).createNewConditionalContent(newContent);
        if (newConditionalContent == null) {
            return null;
        }
        return (RobotConditionalContent) element.replace(newConditionalContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull RobotConditionalContent element) {
        return element.getInjectionRelevantTextRange();
    }
}
