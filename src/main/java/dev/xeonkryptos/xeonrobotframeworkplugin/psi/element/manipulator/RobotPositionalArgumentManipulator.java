package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPositionalArgumentManipulator extends AbstractElementManipulator<RobotPositionalArgument> {

    @Nullable
    @Override
    public RobotPositionalArgument handleContentChange(@NotNull RobotPositionalArgument positionalArgument, @NotNull TextRange textRange, String newText) throws
                                                                                                                                                          IncorrectOperationException {
        String original = positionalArgument.getText();
        String newContent = textRange.replace(original, newText);

        RobotPositionalArgument newPositionalArgument = RobotElementGenerator.getInstance(positionalArgument.getProject())
                                                                             .createNewPositionalArgument(newContent);
        if (newPositionalArgument == null) {
            return null;
        }
        return (RobotPositionalArgument) positionalArgument.replace(newPositionalArgument);
    }
}
