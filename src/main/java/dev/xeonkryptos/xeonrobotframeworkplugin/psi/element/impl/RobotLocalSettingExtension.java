package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import org.jetbrains.annotations.NotNull;

public abstract class RobotLocalSettingExtension extends RobotPsiElementBase implements RobotLocalSetting {

    public RobotLocalSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getNameIdentifier().getName();
    }
}
