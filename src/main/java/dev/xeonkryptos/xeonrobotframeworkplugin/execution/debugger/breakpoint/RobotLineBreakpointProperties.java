package dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger.breakpoint;

import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotLineBreakpointProperties extends XBreakpointProperties<RobotLineBreakpointProperties> {

    @Nullable
    @Override
    public RobotLineBreakpointProperties getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull RobotLineBreakpointProperties state) {}
}
