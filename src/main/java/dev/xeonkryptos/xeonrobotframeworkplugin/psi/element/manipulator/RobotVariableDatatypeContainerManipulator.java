package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDatatypeContainer;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableDatatypeContainerManipulator extends AbstractElementManipulator<RobotVariableDatatypeContainer> {

    @Nullable
    @Override
    public RobotVariableDatatypeContainer handleContentChange(@NotNull RobotVariableDatatypeContainer element, @NotNull TextRange range, String newText) throws IncorrectOperationException {
        String original = element.getText();
        String newContent = range.replace(original, newText);

        RobotVariableDatatypeContainer newVariableDatatype = RobotElementGenerator.getInstance(element.getProject()).createNewVariableDatatypeContainer(newContent);
        if (newVariableDatatype == null) {
            return null;
        }
        RobotVariableDatatypeContainer replacedElement = (RobotVariableDatatypeContainer) element.replace(newVariableDatatype);
        // it is a hack to preserve the `QUICK_EDIT_HANDLERS` key,
        // actually `element.replace` should have done it, but for some reason didn't
        ((UserDataHolderBase) element.getNode()).copyCopyableDataTo((UserDataHolderBase) replacedElement.getNode());
        CodeEditUtil.setNodeGenerated(replacedElement.getNode(), true);
        return replacedElement;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull RobotVariableDatatypeContainer element) {
        return element.getInjectionRelevantTextRange();
    }
}
