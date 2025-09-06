package dev.xeonkryptos.xeonrobotframeworkplugin.util;

import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;

public enum RobotTag {

    CONTINUE_ON_FAILURE("robot:continue-on-failure", new RobotVersion(0, 0, 0)),
    RECURSIVE_CONTINUE_ON_FAILURE("robot:recursive-continue-on-failure", new RobotVersion(0, 0, 0)),
    STOP_ON_FAILURE("robot:stop-on-failure", new RobotVersion(6, 0, 0)),
    RECURSIVE_STOP_ON_FAILURE("robot:recursive-stop-on-failure", new RobotVersion(6, 0, 0)),
    EXIT_ON_FAILURE("robot:exit-on-failure", new RobotVersion(7, 0, 0)),
    SKIP_ON_FAILURE("robot:skip-on-failure", new RobotVersion(0, 0, 0)),
    SKIP("robot:skip", new RobotVersion(0, 0, 0)),
    EXCLUDE("robot:exclude", new RobotVersion(0, 0, 0)),
    PRIVATE("robot:private", new RobotVersion(6, 0, 0)),
    NO_DRY_RUN("robot:no-dry-run", new RobotVersion(0, 0, 0)),
    EXIT("robot:exit", new RobotVersion(0, 0, 0)),
    FLATTEN("robot:flatten", new RobotVersion(6, 1, 0));

    private final String tag;
    private final RobotVersion sinceVersion;

    RobotTag(String tag, RobotVersion sinceVersion) {
        this.tag = tag;
        this.sinceVersion = sinceVersion;
    }

    public String getTag() {
        return tag;
    }

    public RobotVersion getSinceVersion() {
        return sinceVersion;
    }
}
