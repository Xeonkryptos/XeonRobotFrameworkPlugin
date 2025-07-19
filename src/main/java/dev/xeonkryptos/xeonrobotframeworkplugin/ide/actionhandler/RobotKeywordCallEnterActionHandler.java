package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;

public class RobotKeywordCallEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<RobotKeywordCall> {

    public RobotKeywordCallEnterActionHandler() {
        super(RobotKeywordCall.class);
    }
}
