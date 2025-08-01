package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class RobotElementTreeNode extends BasePsiNode<PsiNamedElement> {

    private final RobotViewElementType type;

    public RobotElementTreeNode(Project project, PsiNamedElement element, ViewSettings viewSettings, RobotViewElementType type) {
        super(project, element, viewSettings);

        this.type = type;
    }

    @Override
    protected void updateImpl(@NotNull PresentationData data) {
        PsiNamedElement namedElement = getValue();
        if (namedElement != null) {
            String keywordName = namedElement.getName();
            data.setPresentableText(keywordName);
            data.setIcon(type.getIcon(namedElement));
        }
    }

    @Override
    public Collection<AbstractTreeNode<?>> getChildrenImpl() {
        PsiNamedElement element = getValue();

        RobotSectionElementsCollector treeNodeCollector = new RobotSectionElementsCollector();
        element.acceptChildren(treeNodeCollector);

        Collection<AbstractTreeNode<?>> children = new ArrayList<>();
        for (RobotSection section : treeNodeCollector.getSections()) {
            children.add(new RobotElementTreeNode(getProject(), section, getSettings(), RobotViewElementType.Section));
        }
        for (RobotGlobalSettingStatement statement : treeNodeCollector.getMetadataStatements()) {
            children.add(new RobotElementTreeNode(getProject(), (PsiNamedElement) statement, getSettings(), RobotViewElementType.Settings));
        }

        for (RobotUserKeywordStatement userKeywordStatement : treeNodeCollector.getUserKeywordStatements()) {
            children.add(new RobotElementTreeNode(getProject(), userKeywordStatement, getSettings(), RobotViewElementType.Keyword));
        }

        for (RobotTestCaseStatement testCase : treeNodeCollector.getTestCases()) {
            children.add(new RobotElementTreeNode(getProject(), testCase, getSettings(), RobotViewElementType.TestCase));
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
