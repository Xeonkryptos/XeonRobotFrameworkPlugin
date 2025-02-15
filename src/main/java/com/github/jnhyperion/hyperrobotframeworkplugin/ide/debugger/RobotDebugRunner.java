package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger.dap.RobotDebugAdapterProtocolCommunicator;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.RobotPythonScriptCommandLineState;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.RobotRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
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

public class RobotDebugRunner extends PyDebugRunner {

    private ExecutionResult executionResult;

    @NonNls
    @NotNull
    @Override
    public String getRunnerId() {
        return "com.github.jnhyperion.hyperrobotframeworkplugin.HyperRobotFrameworkDebugRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        if (super.canRun(executorId, profile) && profile instanceof RobotRunConfiguration) {
            return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId);
        }
        return false;
    }

    @Override
    protected @NotNull Promise<@Nullable RunContentDescriptor> execute(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state) throws
                                                                                                                                                  ExecutionException {
        return super.execute(environment, state).then(result -> {
            Project project = environment.getProject();
            try {
                XDebuggerManager debuggerManager = XDebuggerManager.getInstance(project);
                debuggerManager.startSession(environment, new XDebugProcessStarter() {
                    @NotNull
                    @Override
                    public XDebugProcess start(@NotNull final XDebugSession session) {
                        Integer robotDebugPort = ((RobotPythonScriptCommandLineState) state).getRobotDebugPort();
                        RobotDebugAdapterProtocolCommunicator robotDebugAdapterProtocolCommunicator = new RobotDebugAdapterProtocolCommunicator(robotDebugPort);
                        executionResult.getProcessHandler().addProcessListener(robotDebugAdapterProtocolCommunicator);
                        return new RobotDebugProcess(session, executionResult, robotDebugAdapterProtocolCommunicator);
                    }
                });
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            return result;
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
}
