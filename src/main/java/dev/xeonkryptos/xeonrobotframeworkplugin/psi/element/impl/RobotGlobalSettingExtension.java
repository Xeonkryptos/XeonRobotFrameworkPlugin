package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotGlobalSettingExtension extends RobotPsiElementBase implements RobotGlobalSettingStatement {

    public RobotGlobalSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public FoldingDescriptor[] fold(@NotNull Document document) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return null;
        }
        var foldingDescriptor = RobotFoldingComputationUtil.computeFoldingDescriptorForContainer(this, getFirstChild(), document);
        return foldingDescriptor != null ? new FoldingDescriptor[] { foldingDescriptor } : null;
    }
}
