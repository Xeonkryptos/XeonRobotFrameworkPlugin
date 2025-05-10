package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Heading;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

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
        for (Heading heading : PsiTreeUtil.getChildrenOfTypeAsList(psiFile, Heading.class)) {
            if (!heading.isSettings()) {
                for (KeywordDefinition testCase : heading.getTestCases()) {
                    children.add(new RobotElementTreeNode(getProject(), testCase, getSettings(), RobotViewElementType.Keyword));
                }
                for (VariableDefinition variableDefinition : heading.getVariableDefinitions()) {
                    children.add(new RobotElementTreeNode(getProject(), variableDefinition, getSettings(), RobotViewElementType.Variable));
                }
            }
        }
        return children;
    }
}
