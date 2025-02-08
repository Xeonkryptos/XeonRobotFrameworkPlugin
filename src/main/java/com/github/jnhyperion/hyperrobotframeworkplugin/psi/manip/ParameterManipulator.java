package com.github.jnhyperion.hyperrobotframeworkplugin.psi.manip;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Parameter;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParameterManipulator extends AbstractElementManipulator<Parameter> {

    @Override
    public @Nullable Parameter handleContentChange(@NotNull Parameter parameter, @NotNull TextRange textRange, String newContent) throws IncorrectOperationException {
        return null;
    }
}
