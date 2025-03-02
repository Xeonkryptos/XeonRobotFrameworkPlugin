package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.ParamsGroup;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.TargetEnvironment;
import com.intellij.execution.target.value.TargetEnvironmentFunctions;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Ref;
import com.intellij.util.ArrayUtil;
import com.intellij.util.net.NetUtils;
import com.jetbrains.python.PyBundle;
import com.jetbrains.python.run.CommandLinePatcher;
import com.jetbrains.python.run.PythonCommandLineState;
import com.jetbrains.python.run.PythonExecution;
import com.jetbrains.python.run.PythonModuleExecution;
import com.jetbrains.python.run.PythonScriptCommandLineState;
import com.jetbrains.python.run.PythonScriptExecution;
import com.jetbrains.python.run.PythonScriptTargetedCommandLineBuilder;
import com.jetbrains.python.run.target.HelpersAwareTargetEnvironmentRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RobotPythonScriptCommandLineState extends PythonScriptCommandLineState {

    public static final Key<Integer> ROBOT_DEBUG_PORT = Key.create("ROBOT_DEBUG_PORT");

    private static final Path DATA_DIR = PathManager.getPluginsDir().resolve("Hyper RobotFramework Support").resolve("data");
    private static final Path BUNDLED_DIR = DATA_DIR.resolve("bundled");
    private static final Path TOOL_DIR = BUNDLED_DIR.resolve("tool");
    private static final Path ROBOTCODE_DIR = TOOL_DIR.resolve("robotcode");

    private static final int DEBUGGER_DEFAULT_PORT = 6612;

    private final RobotRunConfiguration robotRunConfiguration;

    public RobotPythonScriptCommandLineState(RobotRunConfiguration robotRunConfiguration, @NotNull ExecutionEnvironment env) {
        super(robotRunConfiguration, env);

        this.robotRunConfiguration = robotRunConfiguration;
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, PythonProcessStarter processStarter, CommandLinePatcher... patchers) throws ExecutionException {
        final RobotExecutionMode executionMode = computeRobotExecutionMode(executor);
        return super.execute(executor, processStarter, ArrayUtil.append(patchers, commandLine -> {
            ParametersList parametersList = commandLine.getParametersList();
            ParamsGroup moduleGroup = parametersList.getParamsGroup(PythonCommandLineState.GROUP_MODULE);
            if (moduleGroup != null && moduleGroup.getParameters().contains("robotcode")) {
                modifyCommandLine(moduleGroup, 1, executionMode);
            } else {
                ParamsGroup scriptGroup = parametersList.getParamsGroup(PythonCommandLineState.GROUP_SCRIPT);
                if (scriptGroup != null && scriptGroup.getParameters().contains("robotcode")) {
                    modifyCommandLine(scriptGroup, 0, executionMode);
                }
            }
        }));
    }

    private void modifyCommandLine(ParamsGroup paramsGroup, int scriptIndex, RobotExecutionMode robotExecutionMode) {
        paramsGroup.getParametersList().set(scriptIndex, ROBOTCODE_DIR + "/robotcode");
        if (robotExecutionMode == RobotExecutionMode.DEBUG) {
            int robotDebugPort = findAvailableSocketPort();
            robotRunConfiguration.putUserData(ROBOT_DEBUG_PORT, robotDebugPort);

            paramsGroup.getParametersList().addAll("debug", "--tcp", String.valueOf(robotDebugPort));
        } else {
            paramsGroup.getParametersList().add("robot");
            if (robotExecutionMode == RobotExecutionMode.DRY_RUN) {
                paramsGroup.getParametersList().add("--dryrun");
            }
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("UnstableApiUsage") // Might be unstable at the moment, but is an important extension point
    public ExecutionResult execute(@NotNull Executor executor, @NotNull PythonScriptTargetedCommandLineBuilder converter) throws ExecutionException {
        final RobotExecutionMode executionMode = computeRobotExecutionMode(executor);
        return super.execute(executor, new MyPythonScriptTargetedCommandLineBuilder(converter, robotRunConfiguration, executionMode));
    }

    @SuppressWarnings("UnstableApiUsage") // Might be unstable at the moment, but is an important extension point
    private record MyPythonScriptTargetedCommandLineBuilder(@NotNull PythonScriptTargetedCommandLineBuilder parentBuilder,
                                                            RobotRunConfiguration configuration,
                                                            RobotExecutionMode executionMode) implements PythonScriptTargetedCommandLineBuilder {

        @NotNull
        @Override
        public PythonExecution build(@NotNull HelpersAwareTargetEnvironmentRequest helpersAwareTargetEnvironmentRequest,
                                     @NotNull PythonExecution pythonExecution) {
            Ref<Boolean> robotCodeModuleRef = new Ref<>(false);
            pythonExecution.accept(new PythonExecution.Visitor() {
                @Override
                public void visit(@NotNull PythonScriptExecution pythonScriptExecution) {
                    robotCodeModuleRef.set(false);
                }

                @Override
                public void visit(@NotNull PythonModuleExecution pythonModuleExecution) {
                    boolean robotcodeModule = "robotcode".equals(pythonModuleExecution.getModuleName());
                    robotCodeModuleRef.set(robotcodeModule);
                }
            });

            if (!robotCodeModuleRef.get()) {
                return parentBuilder.build(helpersAwareTargetEnvironmentRequest, pythonExecution);
            }

            List<Function<TargetEnvironment, String>> additionalParameters = new ArrayList<>();
            if (executionMode == RobotExecutionMode.DEBUG) {
                int robotDebugPort = findAvailableSocketPort();
                configuration.putUserData(ROBOT_DEBUG_PORT, robotDebugPort);

                additionalParameters.add(TargetEnvironmentFunctions.constant("debug"));
                additionalParameters.add(TargetEnvironmentFunctions.constant("--tcp"));
                additionalParameters.add(TargetEnvironmentFunctions.constant(String.valueOf(robotDebugPort)));
            } else {
                additionalParameters.add(TargetEnvironmentFunctions.constant("robot"));
                if (executionMode == RobotExecutionMode.DRY_RUN) {
                    additionalParameters.add(TargetEnvironmentFunctions.constant("--dryrun"));
                }
            }

            PythonScriptExecution delegateExecution = createCopiedPythonScriptExecution(pythonExecution);
            delegateExecution.setPythonScriptPath(TargetEnvironmentFunctions.constant(ROBOTCODE_DIR.toString()));
            List<Function<TargetEnvironment, String>> parameters = delegateExecution.getParameters();
            parameters.addAll(0, additionalParameters);

            return parentBuilder.build(helpersAwareTargetEnvironmentRequest, delegateExecution);
        }

        @NotNull
        private static PythonScriptExecution createCopiedPythonScriptExecution(@NotNull PythonExecution pythonExecution) {
            PythonScriptExecution delegateExecution = new PythonScriptExecution();
            pythonExecution.getParameters().forEach(delegateExecution::addParameter);

            File inputFile = pythonExecution.getInputFile();
            delegateExecution.setInputFile(inputFile);

            Charset charset = pythonExecution.getCharset();
            delegateExecution.setCharset(charset);

            pythonExecution.getEnvs().forEach(delegateExecution::addEnvironmentVariable);

            Function<TargetEnvironment, ? extends String> workingDir = pythonExecution.getWorkingDir();
            delegateExecution.setWorkingDir(workingDir);
            return delegateExecution;
        }
    }

    private static RobotExecutionMode computeRobotExecutionMode(Executor executor) {
        return switch (executor.getId()) {
            case DefaultDebugExecutor.EXECUTOR_ID -> RobotExecutionMode.DEBUG;
            case RobotDryRunExecutor.EXECUTOR_ID -> RobotExecutionMode.DRY_RUN;
            default -> RobotExecutionMode.RUN;
        };
    }

    private static int findAvailableSocketPort() {
        try (ServerSocket serverSocket = new ServerSocket(DEBUGGER_DEFAULT_PORT)) {
            // workaround for linux : calling close() immediately after opening socket
            // may result that socket is not closed
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (serverSocket) {
                try {
                    //noinspection WaitNotInLoop
                    serverSocket.wait(1);
                } catch (InterruptedException ignored) {}
            }
            return serverSocket.getLocalPort();
        } catch (Exception ignored) {}
        try {
            return NetUtils.findAvailableSocketPort();
        } catch (IOException e) {
            throw new RuntimeException(PyBundle.message("runcfg.error.message.failed.to.find.free.socket.port"), e);
        }
    }

    public Integer getRobotDebugPort() {
        return robotRunConfiguration.getUserData(ROBOT_DEBUG_PORT);
    }

    private enum RobotExecutionMode {

        RUN, DEBUG, DRY_RUN
    }
}
