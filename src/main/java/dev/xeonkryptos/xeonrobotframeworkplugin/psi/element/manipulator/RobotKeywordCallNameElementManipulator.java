package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallNameElementManipulator extends AbstractElementManipulator<RobotKeywordCallName> {

    @Nullable
    @Override
    public RobotKeywordCallName handleContentChange(@NotNull RobotKeywordCallName keywordCallName, @NotNull TextRange textRange, String newText) throws
                                                                                                                                                 IncorrectOperationException {
        String original = keywordCallName.getText();
        Project project = keywordCallName.getProject();
        newText = KeywordUtil.getInstance(project).functionToKeyword(newText);
        String newContent = textRange.replace(original, newText);

        RobotKeywordCallName newKeywordCallName = RobotElementGenerator.getInstance(project).createNewKeywordCallName(newContent);
        if (newKeywordCallName == null) {
            return null;
        }
        return (RobotKeywordCallName) keywordCallName.replace(newKeywordCallName);
    }
}
