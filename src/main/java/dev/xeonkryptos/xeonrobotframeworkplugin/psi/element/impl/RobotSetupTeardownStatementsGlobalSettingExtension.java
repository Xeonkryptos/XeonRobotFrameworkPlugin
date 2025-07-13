package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting;
import org.jetbrains.annotations.NotNull;

public abstract class RobotSetupTeardownStatementsGlobalSettingExtension extends RobotGlobalSettingStatementImpl implements
                                                                                                                 RobotSetupTeardownStatementsGlobalSetting {

    public RobotSetupTeardownStatementsGlobalSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getNameIdentifier().getText();
    }
}
