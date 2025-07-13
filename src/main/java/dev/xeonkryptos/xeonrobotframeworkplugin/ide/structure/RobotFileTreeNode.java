package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;

import java.util.ArrayList;
import java.util.Collection;

public class RobotFileTreeNode extends PsiFileNode {

    public RobotFileTreeNode(Project project, PsiFile value, ViewSettings viewSettings) {
        super(project, value, viewSettings);
    }

    @Override
    public Collection<AbstractTreeNode<?>> getChildrenImpl() {
        PsiFile psiFile = getValue();

        RobotSectionElementsCollector treeNodeCollector = new RobotSectionElementsCollector();
        psiFile.acceptChildren(treeNodeCollector);

        Collection<AbstractTreeNode<?>> children = new ArrayList<>();
        for (RobotSection section : treeNodeCollector.getSections()) {
            children.add(new RobotElementTreeNode(getProject(), section, getSettings(), RobotViewElementType.Section));
        }
        return children;
    }
}
