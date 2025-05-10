package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.RobotCommandLineState;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.RobotPythonCommandLineState;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.xdebugger.XDebugSession;
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
            return new RobotPyDebugProcess(session,
                                           serverSocket,
                                           result.getExecutionConsole(),
                                           result.getProcessHandler(),
                                           pyState.isMultiprocessDebug(),
                                           robotPythonCommandLineState);
        }

        @NotNull
        @Override
        protected PyDebugProcess createDebugProcess(@NotNull XDebugSession session, int serverPort, ExecutionResult result) {
            return new RobotPyDebugProcess(session, result, serverPort, robotPythonCommandLineState);
        }
    }
}
