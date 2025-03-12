package com.github.jnhyperion.hyperrobotframeworkplugin.psi.manip;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterId;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParameterIdManipulator extends AbstractElementManipulator<ParameterId> {

    @Nullable
    @Override
    public ParameterId handleContentChange(@NotNull ParameterId parameterId, @NotNull TextRange textRange, String newContent) throws
                                                                                                                              IncorrectOperationException {
        return null;
    }
}
