package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RobotLocalSettingExtension extends RobotPsiElementBase implements RobotLocalSetting {

    public RobotLocalSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public FoldingDescriptor[] fold(@NotNull Document document) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return null;
        }
        RobotLocalSettingId localSettingId = getLocalSettingId();
        String settingName = getSettingName();
        List<RobotPositionalArgument> positionalArguments = getPositionalArgumentList();
        if (positionalArguments.isEmpty() || !settingName.equalsIgnoreCase(RobotNames.TAGS_LOCAL_SETTING_NAME)) {
            FoldingDescriptor foldingDescriptor = RobotFoldingComputationUtil.computeFoldingDescriptorForContainer(this, localSettingId, document);
            return foldingDescriptor != null ? new FoldingDescriptor[] { foldingDescriptor } : null;
        }

        List<FoldingDescriptor> foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForListing(getNode(),
                                                                                                                     "LocalSettingTagListFolding",
                                                                                                                     localSettingId,
                                                                                                                     positionalArguments,
                                                                                                                     document);
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : null;
    }
}
