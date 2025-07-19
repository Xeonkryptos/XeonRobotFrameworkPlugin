package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;

public class RobotLocalSettingEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<RobotLocalSetting> {

    public RobotLocalSettingEnterActionHandler() {
        super(RobotLocalSetting.class);
    }
}
