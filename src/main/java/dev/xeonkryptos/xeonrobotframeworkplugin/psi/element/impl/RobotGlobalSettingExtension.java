package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;

public abstract class RobotGlobalSettingExtension extends RobotPsiElementBase implements RobotGlobalSettingStatement {

    public RobotGlobalSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull FoldingDescriptor @NotNull [] fold(@NotNull Document document, boolean quick) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }
        var foldingDescriptor = RobotFoldingComputationUtil.computeFoldingDescriptorForContainer(this, getFirstChild(), document);
        return foldingDescriptor != null ? new FoldingDescriptor[] { foldingDescriptor } : FoldingDescriptor.EMPTY_ARRAY;
    }
}
