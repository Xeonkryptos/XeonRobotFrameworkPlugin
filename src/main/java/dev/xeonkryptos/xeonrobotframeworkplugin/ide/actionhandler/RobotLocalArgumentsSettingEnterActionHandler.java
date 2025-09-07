package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;

public class RobotLocalArgumentsSettingEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<RobotLocalArgumentsSetting> {

    public RobotLocalArgumentsSettingEnterActionHandler() {
        super(RobotLocalArgumentsSetting.class);
    }
}
