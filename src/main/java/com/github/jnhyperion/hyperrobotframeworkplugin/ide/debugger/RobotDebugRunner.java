package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger.dap.RobotDebugAdapterProtocolCommunicator;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.RobotCommandLineState;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.RobotPythonCommandLineState;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.RobotRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.jetbrains.python.debugger.PyDebugProcess;
import com.jetbrains.python.debugger.PyDebugRunner;
import com.jetbrains.python.run.PythonCommandLineState;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

import java.net.ServerSocket;

public class RobotDebugRunner implements ProgramRunner<RunnerSettings> {

    @NonNls
    @NotNull
    @Override
    public String getRunnerId() {
        return "com.github.jnhyperion.hyperrobotframeworkplugin.HyperRobotFrameworkDebugRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return profile instanceof RobotRunConfiguration && (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId));
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        new CustomPyDebugRunner().execute(environment);
    }

    private static class CustomPyDebugRunner extends PyDebugRunner {

        private RobotPythonCommandLineState robotPythonCommandLineState;

        @NotNull
        @Override
        protected Promise<@Nullable RunContentDescriptor> execute(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state) throws
                                                                                                                                             ExecutionException {
            if (state instanceof RobotCommandLineState) {
                state = new RobotPythonCommandLineState(((RobotCommandLineState) state).getRobotRunConfiguration(), environment);
            }
            robotPythonCommandLineState = (RobotPythonCommandLineState) state;
            return super.execute(environment, state);
        }

        @NotNull
        @Override
        protected PyDebugProcess createDebugProcess(@NotNull XDebugSession session,
                                                    ServerSocket serverSocket,
                                                    ExecutionResult result,
                                                    PythonCommandLineState pyState) {
            return new MyPyDebugProcess(session,
                                        serverSocket,
                                        result.getExecutionConsole(),
                                        result.getProcessHandler(),
                                        pyState.isMultiprocessDebug(),
                                        robotPythonCommandLineState);
        }

        @NotNull
        @Override
        protected PyDebugProcess createDebugProcess(@NotNull XDebugSession session, int serverPort, ExecutionResult result) {
            return new MyPyDebugProcess(session, result, serverPort, robotPythonCommandLineState);
        }
    }

    private static class MyPyDebugProcess extends PyDebugProcess {

        private final RobotPythonCommandLineState robotPythonCommandLineState;

        private final RobotDebugProcess robotDebugProcess;

        public MyPyDebugProcess(@NotNull XDebugSession session,
                                @NotNull ServerSocket serverSocket,
                                @NotNull ExecutionConsole executionConsole,
                                @Nullable ProcessHandler processHandler,
                                boolean multiProcess,
                                RobotPythonCommandLineState robotPythonCommandLineState) {
            super(session, serverSocket, executionConsole, processHandler, multiProcess);

            this.robotPythonCommandLineState = robotPythonCommandLineState;
            this.robotDebugProcess = initRobotProcess();
        }

        public MyPyDebugProcess(@NotNull XDebugSession session,
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
}
