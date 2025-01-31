package com.github.jnhyperion.hyperrobotframeworkplugin.psi.manip;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Argument;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariableManipulator extends AbstractElementManipulator<Argument> {

    @Override
    public @Nullable Argument handleContentChange(@NotNull Argument argument, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        return null;
    }
}
