package dev.xeonkryptos.xeonrobotframeworkplugin.psi.manip;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordInvokable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeywordInvokableManipulator extends AbstractElementManipulator<KeywordInvokable> {

    @Nullable
    @Override
    public KeywordInvokable handleContentChange(@NotNull KeywordInvokable keywordInvokable, @NotNull TextRange textRange, String newContent) throws
                                                                                                                                             IncorrectOperationException {
        return null;
    }
}
