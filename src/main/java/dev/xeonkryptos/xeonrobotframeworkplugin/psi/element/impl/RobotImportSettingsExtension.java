package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotFoldingSettings;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportSettings;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;

public abstract class RobotImportSettingsExtension extends RobotPsiElementBase implements RobotImportSettings {

    public RobotImportSettingsExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull FoldingDescriptor[] fold(@NotNull Document document, boolean quick) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }
        var foldingDescriptor = RobotFoldingComputationUtil.computeFoldingDescriptorForEntryHolder(this,
                                                                                                   RobotBundle.message("folding.group.import.display.text") + " "
                                                                                                   + RobotFoldingComputationUtil.CONTAINER_FOLDING_PLACEHOLDER,
                                                                                                   document,
                                                                                                   RobotFoldingSettings.getInstance().getState().getCollapseImports());
        return foldingDescriptor != null ? new FoldingDescriptor[] { foldingDescriptor } : FoldingDescriptor.EMPTY_ARRAY;
    }
}
