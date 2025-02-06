package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.jetbrains.python.debugger.PyBreakpointHandlerFactory;
import com.jetbrains.python.debugger.PyDebugProcess;

public class RobotBreakpointHandlerFactory extends PyBreakpointHandlerFactory {

    @Override
    public XBreakpointHandler createBreakpointHandler(PyDebugProcess pyDebugProcess) {
        return new RobotLineBreakpointHandler(pyDebugProcess);
    }
}
