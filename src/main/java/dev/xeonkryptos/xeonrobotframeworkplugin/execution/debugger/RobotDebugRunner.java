package dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.python.debugger.PyDebugProcess;
import com.jetbrains.python.debugger.PyDebugRunner;
import com.jetbrains.python.parser.icons.PythonParserIcons;
import com.jetbrains.python.run.PythonCommandLineState;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.RobotPythonCommandLineState;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.lang.reflect.Method;
import java.net.ServerSocket;

public class RobotDebugRunner implements ProgramRunner<RunnerSettings> {

    @NonNls
    @NotNull
    @Override
    public String getRunnerId() {
        return "dev.xeonkryptos.xeonrobotframeworkplugin.RobotDebugRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return profile instanceof RobotRunConfiguration && (DefaultDebugExecutor.EXECUTOR_ID.equals(executorId));
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        RobotRunConfiguration robotRunConfiguration = (RobotRunConfiguration) environment.getRunProfile();
        RunProfile pythonRunProfile = new CustomPythonRunProfile(robotRunConfiguration);
        ExecutionEnvironment pythonEnvironment = new ExecutionEnvironmentBuilder(environment).runProfile(pythonRunProfile).build();
        new CustomPyDebugRunner().execute(pythonEnvironment);
    }

    @RequiredArgsConstructor
    private static class CustomPythonRunProfile implements RunProfile {

        private final RobotRunConfiguration robotRunConfiguration;

        private RobotPythonCommandLineState state;

        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
            if (state == null) {
                state = new RobotPythonCommandLineState(robotRunConfiguration, environment);
            }
            return state;
        }

        @Override
        public @NotNull String getName() {
            return "";
        }

        @Override
        public Icon getIcon() {
            return PythonParserIcons.PythonFile;
        }
    }

    private static class CustomPyDebugRunner extends PyDebugRunner {

        @NotNull
        @Override
        protected PyDebugProcess createDebugProcess(@NotNull XDebugSession session, ServerSocket serverSocket, ExecutionResult result, PythonCommandLineState pyState) {
            return new RobotPyDebugProcess(session, serverSocket, result.getExecutionConsole(), result.getProcessHandler(), pyState.isMultiprocessDebug(), (RobotPythonCommandLineState) pyState);
        }

        @NotNull
        @Override
        @SneakyThrows
        protected PyDebugProcess createDebugProcess(@NotNull XDebugSession session, int serverPort, ExecutionResult result) {
            RunProfile runProfile = session.getRunProfile();
            assert runProfile != null;

            // Have to resolve to reflection here as the method itself is public, but within the implementing class, not in the interface itself. In future versions it changes, too, but for now
            // this is a necessary workaround until upgrading to a higher platform version
            Method getExecutionEnvironment = session.getClass().getMethod("getExecutionEnvironment");
            ExecutionEnvironment executionEnvironment = (ExecutionEnvironment) getExecutionEnvironment.invoke(session);

            RobotPythonCommandLineState state = (RobotPythonCommandLineState) runProfile.getState(executionEnvironment.getExecutor(), executionEnvironment);
            assert state != null;
            return new RobotPyDebugProcess(session, result, serverPort, state);
        }
    }
}
