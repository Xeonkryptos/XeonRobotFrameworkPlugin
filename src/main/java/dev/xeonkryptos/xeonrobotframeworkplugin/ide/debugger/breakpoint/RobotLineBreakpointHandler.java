package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.breakpoint;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.RobotDebugProcess;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import org.jetbrains.annotations.NotNull;

public class RobotLineBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<RobotLineBreakpointProperties>> {

    private final RobotDebugProcess debugProcess;

    public RobotLineBreakpointHandler(@NotNull RobotDebugProcess debugProcess) {
        super(RobotLineBreakpointType.class);

        this.debugProcess = debugProcess;
    }

    @Override
    public void registerBreakpoint(@NotNull XLineBreakpoint<RobotLineBreakpointProperties> breakpoint) {
        debugProcess.registerBreakpoint(breakpoint);
    }

    @Override
    public void unregisterBreakpoint(@NotNull XLineBreakpoint<RobotLineBreakpointProperties> breakpoint, boolean temporary) {
        debugProcess.unregisterBreakpoint(breakpoint);
    }
}
