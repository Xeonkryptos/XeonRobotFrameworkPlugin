package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

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
        return Collections.emptyList();
    }
}
