package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallLibraryNameElementManipulator extends AbstractElementManipulator<RobotKeywordCallLibraryName> {

    @Nullable
    @Override
    public RobotKeywordCallLibraryName handleContentChange(@NotNull RobotKeywordCallLibraryName robotKeywordCallLibraryName,
                                                           @NotNull TextRange textRange,
                                                           String s) throws IncorrectOperationException {
        return null;
    }
}
