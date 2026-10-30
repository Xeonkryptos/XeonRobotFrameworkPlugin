package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.formatter.RobotCodeStyleSettings;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallNameElementManipulator extends AbstractElementManipulator<RobotKeywordCallName> {

    @Nullable
    @Override
    public RobotKeywordCallName handleContentChange(@NotNull RobotKeywordCallName keywordCallName, @NotNull TextRange textRange, String newText) throws
                                                                                                                                                 IncorrectOperationException {
        String original = keywordCallName.getText();
        Project project = keywordCallName.getProject();
        if (CodeStyle.getCustomSettings(keywordCallName.getContainingFile(), RobotCodeStyleSettings.class).CAPITALIZE_KEYWORDS) {
            newText = WordUtils.capitalize(newText);
        }
        String newContent = textRange.replace(original, newText);

        RobotKeywordCallName newKeywordCallName = RobotElementGenerator.getInstance(project).createNewKeywordCallName(newContent);
        if (newKeywordCallName == null) {
            return null;
        }
        return (RobotKeywordCallName) keywordCallName.replace(newKeywordCallName);
    }
}
