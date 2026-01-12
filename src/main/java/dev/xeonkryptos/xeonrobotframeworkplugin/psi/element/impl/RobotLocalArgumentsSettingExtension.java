package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RobotLocalArgumentsSettingExtension extends RobotPsiElementBase implements RobotLocalArgumentsSetting {
    public RobotLocalArgumentsSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable FoldingDescriptor[] fold(@NotNull Document document) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(getTextRange(), document)) {
            return null;
        }
        RobotLocalArgumentsSettingId localArgumentsSettingId = getLocalArgumentsSettingId();
        List<RobotLocalArgumentsSettingParameter> localArgumentsSettingParameters = getLocalArgumentsSettingParameterList();
        List<FoldingDescriptor> foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForListing(getNode(),
                                                                                                                     "LocalArgumentsSettingListFolding",
                                                                                                                     localArgumentsSettingId,
                                                                                                                     localArgumentsSettingParameters);
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : null;
    }
}
