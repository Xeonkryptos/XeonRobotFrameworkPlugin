package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallNameElementManipulator extends AbstractElementManipulator<RobotKeywordCallName> {

    @Nullable
    @Override
    public RobotKeywordCallName handleContentChange(@NotNull RobotKeywordCallName robotKeywordCallName, @NotNull TextRange textRange, String s) throws
                                                                                                                                                IncorrectOperationException {
        return null;
    }
}
