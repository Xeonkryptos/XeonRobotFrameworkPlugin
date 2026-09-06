package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalSettingType;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalizationLoadingMechanism;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalizationTypeMappingProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class RobotLocalSettingExtension extends RobotPsiElementBase implements RobotLocalSetting {

    protected RobotLocalSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull FoldingDescriptor @NotNull [] fold(@NotNull Document document, boolean quick) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }
        RobotLocalSettingId localSettingId = getLocalSettingId();
        String settingName = getSettingName();
        List<RobotPositionalArgument> positionalArguments = getPositionalArgumentList();

        LocalizationTypeMappingProvider localizationTypeMappingProvider = LocalizationLoadingMechanism.getInstance(getProject()).getLocalizationTypeMappingProvider();
        LocalSettingType localSettingType = localizationTypeMappingProvider.getLocalSettingType(settingName);

        if (positionalArguments.isEmpty() || localSettingType != LocalSettingType.TAGS) {
            FoldingDescriptor foldingDescriptor = RobotFoldingComputationUtil.computeFoldingDescriptorForContainer(this, localSettingId, document);
            return foldingDescriptor != null ? new FoldingDescriptor[] { foldingDescriptor } : FoldingDescriptor.EMPTY_ARRAY;
        }

        List<FoldingDescriptor> foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForListing(getNode(),
                                                                                                                     "LocalSettingTagListFolding",
                                                                                                                     localSettingId,
                                                                                                                     positionalArguments,
                                                                                                                     document);
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : FoldingDescriptor.EMPTY_ARRAY;
    }
}
