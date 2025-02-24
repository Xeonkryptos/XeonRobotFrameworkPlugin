package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons.RunConfigurations.TestState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
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
                if (heading != null && !heading.getTestCases().isEmpty()) {
                    AnAction[] actions = ExecutorAction.getActions();
                    return new Info(TestState.Green2,
                                    actions,
                                    elem -> StringUtil.join(ContainerUtil.mapNotNull(actions, action -> getText(action, createActionEvent(elem))), "\n"));
                }
            } else {
                if (RobotTokenTypes.KEYWORD_DEFINITION.equals(type)) {
                    Heading heading = getHeading(element);
                    if (heading != null && (heading.containsTasks() || heading.containsTestCases())) {
                        AnAction[] actions = ExecutorAction.getActions();
                        return new Info(TestState.Run,
                                        actions,
                                        elem -> StringUtil.join(ContainerUtil.mapNotNull(actions, action -> getText(action, createActionEvent(elem))), "\n"));
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
