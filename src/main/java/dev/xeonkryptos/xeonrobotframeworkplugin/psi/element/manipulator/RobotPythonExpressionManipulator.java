package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotPythonExpressionManipulator extends AbstractElementManipulator<RobotPythonExpression> {

    @Nullable
    @Override
    public RobotPythonExpression handleContentChange(@NotNull RobotPythonExpression element, @NotNull TextRange range, String newText) throws IncorrectOperationException {
        String original = element.getText();
        String newContent = range.replace(original, newText);

        RobotPythonExpression newPythonExpression = RobotElementGenerator.getInstance(element.getProject()).createNewPythonExpression(newContent);
        if (newPythonExpression == null) {
            return null;
        }
        RobotPythonExpression replacedElement = (RobotPythonExpression) element.replace(newPythonExpression);
        // it is a hack to preserve the `QUICK_EDIT_HANDLERS` key,
        // actually `element.replace` should have done it, but for some reason didn't
        ((UserDataHolderBase) element.getNode()).copyCopyableDataTo((UserDataHolderBase) replacedElement.getNode());
        CodeEditUtil.setNodeGenerated(replacedElement.getNode(), true);
        return replacedElement;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull RobotPythonExpression element) {
        return element.getInjectionRelevantTextRange();
    }
}
