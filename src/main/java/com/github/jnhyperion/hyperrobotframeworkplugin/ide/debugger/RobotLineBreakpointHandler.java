package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.jetbrains.python.debugger.AbstractLineBreakpointHandler;
import com.jetbrains.python.debugger.PyDebugProcess;
import org.jetbrains.annotations.NotNull;

public class RobotLineBreakpointHandler extends AbstractLineBreakpointHandler {

    public RobotLineBreakpointHandler(@NotNull PyDebugProcess debugProcess) {
        super(RobotLineBreakpoint.class, debugProcess);
    }
}
