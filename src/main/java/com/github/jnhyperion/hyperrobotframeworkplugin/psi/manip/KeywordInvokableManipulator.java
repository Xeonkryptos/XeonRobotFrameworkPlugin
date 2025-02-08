package com.github.jnhyperion.hyperrobotframeworkplugin.psi.manip;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeywordInvokableManipulator extends AbstractElementManipulator<KeywordInvokable> {

    @Override
    public @Nullable KeywordInvokable handleContentChange(@NotNull KeywordInvokable keywordInvokable, @NotNull TextRange textRange, String newContent) throws
                                                                                                                                                       IncorrectOperationException {
        return null;
    }
}
