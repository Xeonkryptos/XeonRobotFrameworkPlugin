package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallIdElementManipulator extends AbstractElementManipulator<RobotKeywordCallId> {

    @Nullable
    @Override
    public RobotKeywordCallId handleContentChange(@NotNull RobotKeywordCallId robotKeywordCallId, @NotNull TextRange textRange, String s) throws
                                                                                                                                          IncorrectOperationException {
        return null;
    }
}
