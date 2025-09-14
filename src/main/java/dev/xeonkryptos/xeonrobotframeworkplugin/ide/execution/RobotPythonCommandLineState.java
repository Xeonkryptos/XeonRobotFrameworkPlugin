package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.ParamsGroup;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.TargetEnvironment;
import com.intellij.execution.target.value.TargetEnvironmentFunctions;
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.ArrayUtil;
import com.intellij.util.net.NetUtils;
import com.jetbrains.python.PyBundle;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.CommandLinePatcher;
import com.jetbrains.python.run.PythonCommandLineState;
import com.jetbrains.python.run.PythonExecution;
import com.jetbrains.python.run.PythonImportErrorFilter;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonScriptCommandLineState;
import com.jetbrains.python.run.PythonScriptExecution;
import com.jetbrains.python.run.PythonScriptTargetedCommandLineBuilder;
import com.jetbrains.python.run.target.HelpersAwareTargetEnvironmentRequest;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui.RobotRerunFailedTestsAction;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui.RobotTestRunnerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RobotPythonCommandLineState extends PythonScriptCommandLineState {

    public static final Key<Integer> ROBOT_DEBUG_PORT = Key.create("ROBOT_DEBUG_PORT");

    private static final Path DATA_DIR = PathManager.getPluginsDir().resolve("Xeon RobotFramework Support").resolve("data");
    private static final Path BUNDLED_DIR = DATA_DIR.resolve("bundled");
    private static final Path TOOL_DIR = BUNDLED_DIR.resolve("tool");
    private static final Path ROBOTCODE_DIR = TOOL_DIR.resolve("robotcode");

    private static final int DEBUGGER_DEFAULT_PORT = 6611;

    @NotNull
    private final RobotRunConfiguration runConfiguration;

    public RobotPythonCommandLineState(RobotRunConfiguration runConfiguration, ExecutionEnvironment env) {
        super(runConfiguration.getPythonRunConfiguration(), env);

        this.runConfiguration = runConfiguration;
        this.setMultiprocessDebug(true);
    }

    // Overridden to provide access to the method from another class (it is protected otherwise)
    @NotNull
    @Override
    public ProcessHandler startProcess() throws ExecutionException {
        return super.startProcess();
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, PythonProcessStarter processStarter, CommandLinePatcher... patchers) throws ExecutionException {
        final RobotExecutionMode executionMode = computeRobotExecutionMode(executor);
        try {
            ExecutionResult executionResult = super.execute(executor, processStarter, ArrayUtil.append(patchers, commandLine -> {
                ParametersList parametersList = commandLine.getParametersList();
                ParamsGroup moduleGroup = parametersList.getParamsGroup(PythonCommandLineState.GROUP_MODULE);
                if (moduleGroup != null) {
                    modifyCommandLine(moduleGroup, executionMode);
                }
            }));
            enrichExecutionResult(executionResult);
            return executionResult;
        } catch (ExecutionException e) {
            throw e;
        } catch (Exception e) {
            MyLogger.logger.error(e);
            return null;
        }
    }

    private void modifyCommandLine(ParamsGroup paramsGroup, RobotExecutionMode robotExecutionMode) {
        paramsGroup.getParametersList().set(1, ROBOTCODE_DIR + "/robotcode");
        if (robotExecutionMode == RobotExecutionMode.DEBUG) {
            int robotDebugPort = findAvailableSocketPort();
            runConfiguration.putUserData(ROBOT_DEBUG_PORT, robotDebugPort);

            paramsGroup.getParametersList().addAll("debug", "--tcp", String.valueOf(robotDebugPort));
        } else {
            paramsGroup.getParametersList().add("robot");
            if (robotExecutionMode == RobotExecutionMode.DRY_RUN) {
                paramsGroup.getParametersList().add("--dryrun");
            }
        }
    }

    @Override
    public void customizeEnvironmentVars(Map<String, String> envs, boolean passParentEnvs) {
        super.customizeEnvironmentVars(envs, passParentEnvs);
        if (((PythonRunConfiguration) getConfig()).emulateTerminal()) {
            envs.put("NO_TEAMCITY", "1");
        }
    }

    @NotNull
    @Override
    protected ConsoleView createAndAttachConsole(Project project, ProcessHandler processHandler, Executor executor) {
        AbstractPythonRunConfiguration<?> config = getConfig();
        ConsoleView smtRunnerConsoleView = RobotTestRunnerFactory.createConsoleView(config, executor);
        smtRunnerConsoleView.attachToProcess(processHandler);

        smtRunnerConsoleView.addMessageFilter(createUrlFilter(processHandler));
        smtRunnerConsoleView.addMessageFilter(new PythonImportErrorFilter(project));
        smtRunnerConsoleView.addMessageFilter(new RobotReportsFilter());

        addTracebackFilter(project, smtRunnerConsoleView, processHandler);
        return smtRunnerConsoleView;
    }

    @Nullable
    @Override
    @SuppressWarnings("UnstableApiUsage") // Might be unstable at the moment, but is an important extension point
    public ExecutionResult execute(@NotNull Executor executor, @NotNull PythonScriptTargetedCommandLineBuilder converter) throws ExecutionException {
        final RobotExecutionMode executionMode = computeRobotExecutionMode(executor);
        var wrappedConverter = new MyPythonScriptTargetedCommandLineBuilder(converter, runConfiguration, executionMode);
        try {
            ExecutionResult executionResult = super.execute(executor, wrappedConverter);
            enrichExecutionResult(executionResult);
            return executionResult;
        } catch (ExecutionException e) {
            throw e;
        } catch (Exception e) {
            MyLogger.logger.error(e);
            return null;
        }
    }

    private void enrichExecutionResult(@Nullable ExecutionResult executionResult) {
        if (runConfiguration.getPythonRunConfiguration().emulateTerminal() && executionResult != null
            && executionResult.getExecutionConsole() instanceof ConsoleView consoleView) {
            consoleView.addMessageFilter(new RobotReportsFilter());
        }
        if (executionResult != null && executionResult.getExecutionConsole() instanceof SMTRunnerConsoleView consoleView) {
            RobotRerunFailedTestsAction rerunFailedTestsAction = new RobotRerunFailedTestsAction(consoleView);
            rerunFailedTestsAction.init(consoleView.getProperties());
            rerunFailedTestsAction.setModelProvider(consoleView::getResultsViewer);

            if (executionResult instanceof DefaultExecutionResult defaultExecutionResult) {
                defaultExecutionResult.setRestartActions(rerunFailedTestsAction, new ToggleAutoTestAction());
            }
        }
    }

    @SuppressWarnings("UnstableApiUsage") // Might be unstable at the moment, but is an important extension point
    private record MyPythonScriptTargetedCommandLineBuilder(@NotNull PythonScriptTargetedCommandLineBuilder parentBuilder,
                                                            RobotRunConfiguration configuration,
                                                            RobotExecutionMode executionMode) implements PythonScriptTargetedCommandLineBuilder {

        @NotNull
        @Override
        public PythonExecution build(@NotNull HelpersAwareTargetEnvironmentRequest helpersAwareTargetEnvironmentRequest,
                                     @NotNull PythonExecution pythonExecution) {
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

            if (configuration.getPythonRunConfiguration().emulateTerminal()) {
                delegateExecution.addEnvironmentVariable("NO_TEAMCITY", "1");
            }

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
                } catch (InterruptedException ignored) {
                }
            }
            return serverSocket.getLocalPort();
        } catch (Exception ignored) {
        }
        try {
            return NetUtils.findAvailableSocketPort();
        } catch (IOException e) {
            throw new RuntimeException(PyBundle.message("runcfg.error.message.failed.to.find.free.socket.port"), e);
        }
    }

    public Integer getRobotDebugPort() {
        return runConfiguration.getUserData(ROBOT_DEBUG_PORT);
    }

    private enum RobotExecutionMode {

        RUN, DEBUG, DRY_RUN
    }
}
