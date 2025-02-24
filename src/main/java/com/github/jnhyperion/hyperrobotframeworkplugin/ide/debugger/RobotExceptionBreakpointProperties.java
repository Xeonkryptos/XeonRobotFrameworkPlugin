package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotExceptionBreakpointProperties extends XBreakpointProperties<RobotExceptionBreakpointProperties> {

    @Nullable
    @Override
    public RobotExceptionBreakpointProperties getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull RobotExceptionBreakpointProperties state) {}
}
