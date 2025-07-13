package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import org.jetbrains.annotations.NotNull;

class RobotTypeFilter implements Filter {

    @NotNull
    private final String name;
    @NotNull
    private final RobotViewElementType type;

    RobotTypeFilter(@NotNull String name, @NotNull RobotViewElementType type) {
        super();
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean isVisible(TreeElement element) {
        if (element instanceof RobotStructureViewElement structureViewElement) {
            RobotViewElementType type = structureViewElement.getType();
            return this.type != type;
        }
        return false;
    }

    @Override
    public boolean isReverted() {
        return true;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
        return new ActionPresentationData(this.type.getMessage(), null, this.type.getIcon(null));
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
