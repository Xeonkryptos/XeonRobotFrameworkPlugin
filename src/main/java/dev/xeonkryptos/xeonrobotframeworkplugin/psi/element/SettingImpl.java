package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class SettingImpl extends RobotPsiElementBase implements Setting {

    private SettingType settingType;

    public SettingImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        settingType = null;
    }

    @Override
    public final boolean isSuiteTeardown() {
        return getSettingType() == SettingType.SUITE_TEARDOWN;
    }

    @Override
    public final boolean isTestTeardown() {
        return getSettingType() == SettingType.TEST_TEARDOWN;
    }

    private SettingType getSettingType() {
        if (settingType == null) {
            String presentableText = getPresentableText();
            if ("Suite Teardown".equalsIgnoreCase(presentableText)) {
                settingType = SettingType.SUITE_TEARDOWN;
            } else if ("Test Teardown".equalsIgnoreCase(presentableText)) {
                settingType = SettingType.TEST_TEARDOWN;
            } else {
                settingType = null;
            }
        }
        return settingType;
    }

    private enum SettingType {
        SUITE_TEARDOWN, TEST_TEARDOWN
    }
}
