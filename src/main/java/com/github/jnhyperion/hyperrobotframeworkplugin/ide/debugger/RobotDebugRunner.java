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
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.jetbrains.python.debugger.PyDebugProcess;
import com.jetbrains.python.debugger.PyDebugRunner;
import com.jetbrains.python.run.PythonCommandLineState;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

import java.net.ServerSocket;

public class RobotDebugRunner implements ProgramRunner<RunnerSettings> {

    private final PyDebugRunner pyDebugRunner = new CustomPyDebugRunner();

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
        pyDebugRunner.execute(environment);
    }

    private static class CustomPyDebugRunner extends PyDebugRunner {

        private ExecutionResult executionResult;

        @NotNull
        @Override
        protected Promise<@Nullable RunContentDescriptor> execute(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state) throws
                                                                                                                                             ExecutionException {
            if (state instanceof RobotCommandLineState) {
                state = new RobotPythonCommandLineState(((RobotCommandLineState) state).getRobotRunConfiguration(), environment);
            }
            final RobotPythonCommandLineState robotPythonCommandLineState = (RobotPythonCommandLineState) state;
            return super.execute(environment, state).then(result -> {
                Project project = environment.getProject();
                try {
                    XDebuggerManager debuggerManager = XDebuggerManager.getInstance(project);
                    Integer robotDebugPort = robotPythonCommandLineState.getRobotDebugPort();
                    RobotDebugAdapterProtocolCommunicator robotDebugAdapterProtocolCommunicator = new RobotDebugAdapterProtocolCommunicator(robotDebugPort);
                    RunContentDescriptor runContentDescriptor = debuggerManager.startSession(environment, new XDebugProcessStarter() {
                        @NotNull
                        @Override
                        public XDebugProcess start(@NotNull final XDebugSession session) {
                            return new RobotDebugProcess(session, executionResult, robotDebugAdapterProtocolCommunicator);
                        }
                    }).getRunContentDescriptor();

                    executionResult.getProcessHandler().addProcessListener(robotDebugAdapterProtocolCommunicator);

                    return runContentDescriptor;
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @NotNull
        @Override
        protected PyDebugProcess createDebugProcess(@NotNull XDebugSession session,
                                                    ServerSocket serverSocket,
                                                    ExecutionResult result,
                                                    PythonCommandLineState pyState) {
            executionResult = result;
            return super.createDebugProcess(session, serverSocket, result, pyState);
        }

        @Override
        protected @NotNull PyDebugProcess createDebugProcess(@NotNull XDebugSession session, int serverPort, ExecutionResult result) {
            executionResult = result;
            return super.createDebugProcess(session, serverPort, result);
        }
    }
}
