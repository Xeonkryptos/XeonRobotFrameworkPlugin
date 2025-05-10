package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Heading;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons.RunConfigurations.TestState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotRunLineMarkerProvider extends RunLineMarkerContributor {

    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement element) {
        if (element instanceof LeafPsiElement) {
            IElementType type = ((LeafPsiElement) element).getElementType();
            if (RobotTokenTypes.HEADING.equals(type)) {
                Heading heading = getHeading(element);
                if (heading != null && (heading.containsTestCases() || heading.containsTasks())) {
                    AnAction[] actions = ExecutorAction.getActions();
                    return new Info(TestState.Green2, actions);
                }
            } else {
                if (RobotStubTokenTypes.KEYWORD_DEFINITION.equals(type)) {
                    Heading heading = getHeading(element);
                    if (heading != null && (heading.containsTasks() || heading.containsTestCases())) {
                        AnAction[] actions = ExecutorAction.getActions();
                        return new Info(TestState.Run, actions);
                    }
                }
            }
        }
        return null;
    }

    private Heading getHeading(PsiElement element) {
        if (element instanceof Heading) {
            return (Heading) element;
        }
        if (element != null) {
            return getHeading(element.getParent());
        }
        return null;
    }
}
