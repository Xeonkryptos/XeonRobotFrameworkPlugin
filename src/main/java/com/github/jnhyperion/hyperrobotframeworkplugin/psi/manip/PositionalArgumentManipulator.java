package com.github.jnhyperion.hyperrobotframeworkplugin.psi.manip;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PositionalArgumentManipulator extends AbstractElementManipulator<PositionalArgument> {

    @Override
    public @Nullable PositionalArgument handleContentChange(@NotNull PositionalArgument positionalArgument, @NotNull TextRange textRange, String newContent) throws IncorrectOperationException {
        return null;
    }
}
