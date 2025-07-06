package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;

import java.util.ArrayList;
import java.util.Collection;

public class RobotFileTreeNode extends PsiFileNode {

    public RobotFileTreeNode(Project project, PsiFile value, ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    @Override
    public Collection<AbstractTreeNode<?>> getChildrenImpl() {
        PsiFile psiFile = getValue();
        Collection<AbstractTreeNode<?>> children = new ArrayList<>();
        for (RobotSection robotSection : PsiTreeUtil.getChildrenOfTypeAsList(psiFile, RobotSection.class)) {
            if (!(robotSection instanceof RobotSettingsSection)) {
                RobotSectionElementsCollector treeNodeCollector = new RobotSectionElementsCollector();
                robotSection.acceptChildren(treeNodeCollector);
                for (PsiNamedElement testCase : treeNodeCollector.getTestCases()) {
                    children.add(new RobotElementTreeNode(getProject(), testCase, getSettings(), RobotViewElementType.Keyword));
                }
                for (PsiNamedElement variableDefinition : treeNodeCollector.getVariableStatements()) {
                    children.add(new RobotElementTreeNode(getProject(), variableDefinition, getSettings(), RobotViewElementType.Variable));
                }
            }
        }
        return children;
    }

}
