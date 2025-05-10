package dev.xeonkryptos.xeonrobotframeworkplugin.psi.manip;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariableManipulator extends AbstractElementManipulator<Variable> {

    @Nullable
    @Override
    public Variable handleContentChange(@NotNull Variable argument, @NotNull TextRange textRange, String newContent) throws IncorrectOperationException {
        return null;
    }
}
