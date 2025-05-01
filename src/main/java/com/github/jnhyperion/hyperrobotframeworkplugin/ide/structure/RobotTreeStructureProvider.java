package com.github.jnhyperion.hyperrobotframeworkplugin.ide.structure;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotFeatureFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotResourceFileType;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

public class RobotTreeStructureProvider implements TreeStructureProvider, DumbAware {

    @Override
    public @NotNull Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent,
                                                           @NotNull Collection<AbstractTreeNode<?>> children,
                                                           ViewSettings viewSettings) {
        if (!viewSettings.isShowMembers()) {
            return children;
        }

        Collection<AbstractTreeNode<?>> result = new LinkedHashSet<>(children);
        for (AbstractTreeNode<?> child : children) {
            if (child instanceof PsiFileNode fileNode) {
                PsiFile file = fileNode.getValue();
                VirtualFile virtualFile = file.getVirtualFile();

                if (virtualFile != null && isRobotFile(virtualFile)) {
                    result.remove(child);
                    result.add(new RobotFileTreeNode(fileNode.getProject(), file, viewSettings));
                }
            }
        }

        return result;
    }

    private boolean isRobotFile(@NotNull VirtualFile file) {
        FileType fileType = file.getFileType();
        return fileType == RobotFeatureFileType.getInstance() || fileType == RobotResourceFileType.getInstance();
    }
}
