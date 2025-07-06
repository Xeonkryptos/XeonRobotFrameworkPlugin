package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPositionalArgumentManipulator extends AbstractElementManipulator<RobotPositionalArgument> {

    @Nullable
    @Override
    public RobotPositionalArgument handleContentChange(@NotNull RobotPositionalArgument positionalArgument, @NotNull TextRange textRange, String s) throws
                                                                                                                                                    IncorrectOperationException {
        return null;
    }
}
