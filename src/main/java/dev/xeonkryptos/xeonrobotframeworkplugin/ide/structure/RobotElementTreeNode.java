package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class RobotElementTreeNode extends BasePsiNode<PsiElement> {

    private final RobotViewElementType type;

    public RobotElementTreeNode(Project project, PsiElement element, ViewSettings viewSettings, RobotViewElementType type) {
        super(project, element, viewSettings);

        this.type = type;
    }

    @Override
    protected void updateImpl(@NotNull PresentationData data) {
        PsiElement element = getValue();
        if (element != null) {
            String presentableText = null;
            if (element instanceof NavigationItem navigationItem) {
                ItemPresentation presentation = navigationItem.getPresentation();
                if (presentation != null) {
                    presentableText = presentation.getPresentableText();
                }
            }
            if (presentableText == null) {
                presentableText = element.getText().lines().findFirst().orElse("");
            }
            data.setPresentableText(presentableText);
            data.setIcon(type.getIcon(element));
        }
    }

    @Override
    public Collection<AbstractTreeNode<?>> getChildrenImpl() {
        PsiElement element = getValue();

        RobotSectionElementsCollector treeNodeCollector = new RobotSectionElementsCollector();
        element.acceptChildren(treeNodeCollector);

        Collection<AbstractTreeNode<?>> children = new ArrayList<>();
        for (RobotSection section : treeNodeCollector.getSections()) {
            children.add(new RobotElementTreeNode(getProject(), section, getSettings(), RobotViewElementType.Section));
        }
        for (RobotGlobalSettingStatement statement : treeNodeCollector.getMetadataStatements()) {
            children.add(new RobotElementTreeNode(getProject(), statement, getSettings(), RobotViewElementType.Settings));
        }

        for (RobotUserKeywordStatement userKeywordStatement : treeNodeCollector.getUserKeywordStatements()) {
            children.add(new RobotElementTreeNode(getProject(), userKeywordStatement, getSettings(), RobotViewElementType.Keyword));
        }

        for (RobotTestCaseStatement testCase : treeNodeCollector.getTestCases()) {
            children.add(new RobotElementTreeNode(getProject(), testCase, getSettings(), RobotViewElementType.TestCase));
        }

        for (RobotTaskStatement task : treeNodeCollector.getTasks()) {
            children.add(new RobotElementTreeNode(getProject(), task, getSettings(), RobotViewElementType.Task));
        }

        for (RobotVariableDefinition variableDefinition : treeNodeCollector.getVariableDefinitions()) {
            children.add(new RobotElementTreeNode(getProject(), variableDefinition, getSettings(), RobotViewElementType.Variable));
        }

        for (RobotKeywordCall keywordCall : treeNodeCollector.getKeywordCalls()) {
            children.add(new RobotElementTreeNode(getProject(), keywordCall, getSettings(), RobotViewElementType.Keyword));
        }
        return children;
    }
}
