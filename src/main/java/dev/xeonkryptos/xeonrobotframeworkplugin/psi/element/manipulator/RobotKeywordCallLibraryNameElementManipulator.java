package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallLibraryNameElementManipulator extends AbstractElementManipulator<RobotKeywordCallLibraryName> {

    @Nullable
    @Override
    public RobotKeywordCallLibraryName handleContentChange(@NotNull RobotKeywordCallLibraryName keywordCallLibraryName,
                                                           @NotNull TextRange textRange,
                                                           String newText) throws IncorrectOperationException {
        String original = keywordCallLibraryName.getText();
        String newContent = textRange.replace(original, newText);
        RobotKeywordCallLibraryName newKeywordCallLibraryName = RobotElementGenerator.getInstance(keywordCallLibraryName.getProject())
                                                                                     .createNewKeywordCallLibraryName(newContent);
        if (newKeywordCallLibraryName == null) {
            return null;
        }
        return (RobotKeywordCallLibraryName) keywordCallLibraryName.replace(newKeywordCallLibraryName);
    }
}
