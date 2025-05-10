package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.dap.RobotDebugAdapterProtocolCommunicator;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.RobotPythonCommandLineState;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.jetbrains.python.debugger.PyDebugProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.ServerSocket;

/**
 * This class is a custom implementation of the PyDebugProcess for the Robot Framework. It delegates requests to either the RobotDebugProcess or the
 * PyDebugProcess depending on the context. Or, in some cases, to both of them.
 */
class RobotPyDebugProcess extends PyDebugProcess {

    private final RobotPythonCommandLineState robotPythonCommandLineState;

    private final RobotDebugProcess robotDebugProcess;

    public RobotPyDebugProcess(@NotNull XDebugSession session,
                               @NotNull ServerSocket serverSocket,
                               @NotNull ExecutionConsole executionConsole,
                               @Nullable ProcessHandler processHandler,
                               boolean multiProcess,
                               RobotPythonCommandLineState robotPythonCommandLineState) {
        super(session, serverSocket, executionConsole, processHandler, multiProcess);

        this.robotPythonCommandLineState = robotPythonCommandLineState;
        this.robotDebugProcess = initRobotProcess();
    }

    public RobotPyDebugProcess(@NotNull XDebugSession session,
                               ExecutionResult result,
                               int serverPort,
                               RobotPythonCommandLineState robotPythonCommandLineState) {
        super(session, result.getExecutionConsole(), result.getProcessHandler(), "localhost", serverPort);

        this.robotPythonCommandLineState = robotPythonCommandLineState;
        this.robotDebugProcess = initRobotProcess();
    }

    private RobotDebugProcess initRobotProcess() {
        Integer robotDebugPort = robotPythonCommandLineState.getRobotDebugPort();
        RobotDebugAdapterProtocolCommunicator robotDebugAdapterProtocolCommunicator = new RobotDebugAdapterProtocolCommunicator(robotDebugPort);
        RobotDebugProcess robotDebugProcess = new RobotDebugProcess(getSession(), robotDebugAdapterProtocolCommunicator);

        ProcessHandler processHandler = getProcessHandler();
        processHandler.addProcessListener(robotDebugAdapterProtocolCommunicator);
        if (processHandler.isStartNotified()) {
            // Usually, startNotified would be called by the ProcessHandler itself and in reality, it is called by it. Sadly, when we're reaching this point,
            // the process is already running and the method called. Therefore, we have to emulate the call ourselves to connect to our debug server
            robotDebugAdapterProtocolCommunicator.startNotified(new ProcessEvent(processHandler));
        }
        return robotDebugProcess;
    }

    @Override
    public XBreakpointHandler<?> @NotNull [] getBreakpointHandlers() {
        XBreakpointHandler<?>[] pythonBreakpointHandlers = super.getBreakpointHandlers();
        XBreakpointHandler<?>[] robotBreakpointHandlers = robotDebugProcess.getBreakpointHandlers();
        return ArrayUtil.mergeArrays(pythonBreakpointHandlers, robotBreakpointHandlers);
    }

    @Override
    public void startStepOver(@Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext) {
            robotDebugProcess.startStepOver(context);
        } else {
            super.startStepOver(context);
        }
    }

    @Override
    public void startStepInto(@Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext) {
            robotDebugProcess.startStepInto(context);
        } else {
            super.startStepInto(context);
        }
    }

    @Override
    public void startStepOut(@Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext) {
            robotDebugProcess.startStepOut(context);
        } else {
            super.startStepOut(context);
        }
    }

    @Override
    public void runToPosition(@NotNull XSourcePosition position, @Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext) {
            robotDebugProcess.runToPosition(position, context);
        } else {
            super.runToPosition(position, context);
        }
    }

    @Override
    public void resume(@Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext) {
            robotDebugProcess.resume(context);
        } else {
            super.resume(context);
        }
    }

    @Override
    public void startPausing() {
        robotDebugProcess.startPausing();
        super.startPausing();
    }

    @Override
    public void stop() {
        robotDebugProcess.stop();
        super.stop();
    }
}
