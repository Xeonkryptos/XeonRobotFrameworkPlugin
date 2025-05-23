package dev.xeonkryptos.xeonrobotframeworkplugin.psi.manip;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PositionalArgumentManipulator extends AbstractElementManipulator<PositionalArgument> {

    @Nullable
    @Override
    public PositionalArgument handleContentChange(@NotNull PositionalArgument positionalArgument, @NotNull TextRange textRange, String newContent) throws
                                                                                                                                                   IncorrectOperationException {
        return null;
    }
}
