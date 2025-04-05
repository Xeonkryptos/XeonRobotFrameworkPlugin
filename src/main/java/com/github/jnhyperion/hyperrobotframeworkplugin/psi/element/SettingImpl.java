package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class SettingImpl extends RobotPsiElementBase implements Setting {

    private final SettingType settingType;

    public SettingImpl(@NotNull ASTNode node) {
        super(node);

        String presentableText = getPresentableText();
        if ("Suite Teardown".equalsIgnoreCase(presentableText)) {
            settingType = SettingType.SUITE_TEARDOWN;
        } else if ("Test Teardown".equalsIgnoreCase(presentableText)) {
            settingType = SettingType.TEST_TEARDOWN;
        } else {
            settingType = null;
        }
    }

    @Override
    public final boolean isSuiteTeardown() {
        return settingType == SettingType.SUITE_TEARDOWN;
    }

    @Override
    public final boolean isTestTeardown() {
        return settingType == SettingType.TEST_TEARDOWN;
    }

    private enum SettingType {
        SUITE_TEARDOWN, TEST_TEARDOWN
    }
}
