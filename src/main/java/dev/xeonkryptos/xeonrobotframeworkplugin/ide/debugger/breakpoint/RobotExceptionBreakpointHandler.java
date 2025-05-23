package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.breakpoint;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.RobotDebugProcess;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import org.jetbrains.annotations.NotNull;

public class RobotExceptionBreakpointHandler extends XBreakpointHandler<XBreakpoint<RobotExceptionBreakpointProperties>> {

    private final RobotDebugProcess debugProcess;

    public RobotExceptionBreakpointHandler(@NotNull RobotDebugProcess debugProcess) {
        super(RobotExceptionBreakpointType.class);

        this.debugProcess = debugProcess;
    }

    @Override
    public void registerBreakpoint(@NotNull XBreakpoint<RobotExceptionBreakpointProperties> breakpoint) {
        debugProcess.registerExceptionBreakpoint(breakpoint);
    }

    @Override
    public void unregisterBreakpoint(@NotNull XBreakpoint<RobotExceptionBreakpointProperties> breakpoint, boolean temporary) {
        debugProcess.unregisterExceptionBreakpoint(breakpoint);
    }
}
