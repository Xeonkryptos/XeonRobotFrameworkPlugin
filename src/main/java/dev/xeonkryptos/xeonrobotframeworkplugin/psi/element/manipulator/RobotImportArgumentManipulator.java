package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotImportArgumentManipulator extends AbstractElementManipulator<RobotImportArgument> {

    @Nullable
    @Override
    public RobotImportArgument handleContentChange(@NotNull RobotImportArgument positionalArgument, @NotNull TextRange textRange, String newText) throws IncorrectOperationException {
        String original = positionalArgument.getText();
        String newContent = textRange.replace(original, newText);

        RobotImportArgument newPositionalArgument = RobotElementGenerator.getInstance(positionalArgument.getProject()).createNewImportArgument(newContent);
        if (newPositionalArgument == null) {
            return null;
        }
        return (RobotImportArgument) positionalArgument.replace(newPositionalArgument);
    }
}
