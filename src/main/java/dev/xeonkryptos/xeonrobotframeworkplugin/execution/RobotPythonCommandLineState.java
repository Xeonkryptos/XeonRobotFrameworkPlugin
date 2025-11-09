package dev.xeonkryptos.xeonrobotframeworkplugin.execution;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.ParamsGroup;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.TargetEnvironment;
import com.intellij.execution.target.value.TargetEnvironmentFunctions;
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.util.ArrayUtil;
import com.jetbrains.python.actions.PyExecuteInConsole;
import com.jetbrains.python.actions.PyRunFileInConsoleAction;
import com.jetbrains.python.console.PyConsoleOptions;
import com.jetbrains.python.run.CommandLinePatcher;
import com.jetbrains.python.run.PythonCommandLineState;
import com.jetbrains.python.run.PythonConsoleScripts;
import com.jetbrains.python.run.PythonExecution;
import com.jetbrains.python.run.PythonImportErrorFilter;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonScriptCommandLineState;
import com.jetbrains.python.run.PythonScriptExecution;
import com.jetbrains.python.run.PythonScriptTargetedCommandLineBuilder;
import com.jetbrains.python.run.target.HelpersAwareTargetEnvironmentRequest;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap.RobotDebugAdapterProtocolCommunicator;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration.RobotRunnableUnitExecutionInfo;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.ui.RobotRerunFailedTestsAction;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.BundleUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.NetworkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class RobotPythonCommandLineState extends PythonScriptCommandLineState {

    private static final int DEBUGGER_DEFAULT_PORT = 6611;

    @NotNull
    private final RobotRunConfiguration runConfiguration;

    private volatile RobotDebugAdapterProtocolCommunicator dapCommunicator;

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
        ParametersList parametersList = paramsGroup.getParametersList();
        parametersList.set(1, BundleUtil.ROBOTCODE_DIR.resolve("robotcode").toString());

        int robotDebugPort = NetworkUtil.findAvailableSocketPort(DEBUGGER_DEFAULT_PORT);
        dapCommunicator = new RobotDebugAdapterProtocolCommunicator(robotDebugPort);

        if (robotExecutionMode == RobotExecutionMode.DRY_RUN) {
            parametersList.addAt(2, "--dryrun");
        }

        if (robotExecutionMode != RobotExecutionMode.DEBUG) {
            parametersList.addAt(2, "--no-debug");
        }
        parametersList.addAt(2, String.valueOf(robotDebugPort));
        parametersList.addAt(2, "--tcp");
        parametersList.addAt(2, "debug");

        enrichWithTestArguments(runConfiguration, parametersList::add);
    }

    @NotNull
    @Override
    protected ConsoleView createAndAttachConsole(Project project, ProcessHandler processHandler, Executor executor) throws ExecutionException {
        if (!runConfiguration.getTasks().isEmpty() || showCommandLineAfterwards()) {
            // With existing tasks to execute, don't show the SMT view. Tasks aren't tests.
            ConsoleView consoleView = super.createAndAttachConsole(project, processHandler, executor);
            consoleView.addMessageFilter(new RobotReportsFilter());
            return consoleView;
        }
        ConsoleView smtRunnerConsoleView = RobotTestRunnerFactory.createConsoleView(runConfiguration, executor, this);
        smtRunnerConsoleView.attachToProcess(processHandler);

        smtRunnerConsoleView.addMessageFilter(createUrlFilter(processHandler));
        smtRunnerConsoleView.addMessageFilter(new PythonImportErrorFilter(project));
        smtRunnerConsoleView.addMessageFilter(new RobotReportsFilter());

        addTracebackFilter(project, smtRunnerConsoleView, processHandler);
        return smtRunnerConsoleView;
    }

    @Nullable
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public ExecutionResult execute(@NotNull Executor executor, @NotNull PythonScriptTargetedCommandLineBuilder converter) throws ExecutionException {
        final RobotExecutionMode executionMode = computeRobotExecutionMode(executor);
        ExecutionEnvironment environment = getEnvironment();
        var wrappedConverter = new MyPythonScriptTargetedCommandLineBuilder(converter, runConfiguration, executionMode, environment);
        try {
            String executorId = executor.getId();
            if (showCommandLineAfterwards() && (DefaultRunExecutor.EXECUTOR_ID.equals(executorId) || RobotDryRunExecutor.EXECUTOR_ID.equals(executorId))) {
                Project project = runConfiguration.getProject();
                PythonRunConfiguration pythonRunConfiguration = (PythonRunConfiguration) runConfiguration.getPythonRunConfiguration().clone();
                PyRunFileInConsoleAction.configExecuted(pythonRunConfiguration);

                pythonRunConfiguration.setModuleMode(true);
                pythonRunConfiguration.setScriptName("robot");
                String workingDirectory = pythonRunConfiguration.getWorkingDirectory();
                if (workingDirectory == null || workingDirectory.isBlank()) {
                    pythonRunConfiguration.setWorkingDirectory(pythonRunConfiguration.getWorkingDirectorySafe());
                }

                StringBuilder additionalTestArguments = new StringBuilder();
                enrichWithTestArguments(runConfiguration, argument -> {
                    boolean containsSpaces = argument.contains(" ");
                    if (containsSpaces) {
                        additionalTestArguments.append('"');
                    }
                    additionalTestArguments.append(argument);
                    if (containsSpaces) {
                        additionalTestArguments.append('"');
                    }
                    additionalTestArguments.append(" ");
                });

                String scriptParameters = pythonRunConfiguration.getScriptParameters() + additionalTestArguments.toString().trim();
                pythonRunConfiguration.setScriptParameters(scriptParameters);

                Function<TargetEnvironment, String> runFileText = PythonConsoleScripts.buildScriptFunctionWithConsoleRun(pythonRunConfiguration);
                boolean useExistingConsole = PyConsoleOptions.getInstance(project).isUseExistingConsole();
                PyExecuteInConsole.executeCodeInConsole(project, runFileText, null, useExistingConsole, false, true, pythonRunConfiguration);

                return null;
            }
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

    @SuppressWarnings("UnstableApiUsage")
    private class MyPythonScriptTargetedCommandLineBuilder implements PythonScriptTargetedCommandLineBuilder {

        private final PythonScriptTargetedCommandLineBuilder parentBuilder;
        private final RobotRunConfiguration configuration;
        private final RobotExecutionMode executionMode;
        private final ExecutionEnvironment environment;

        private MyPythonScriptTargetedCommandLineBuilder(@NotNull PythonScriptTargetedCommandLineBuilder parentBuilder,
                                                         RobotRunConfiguration configuration,
                                                         RobotExecutionMode executionMode,
                                                         ExecutionEnvironment environment) {
            this.parentBuilder = parentBuilder;
            this.configuration = configuration;
            this.executionMode = executionMode;
            this.environment = environment;
        }

        @NotNull
        @Override
        public PythonExecution build(@NotNull HelpersAwareTargetEnvironmentRequest helpersAwareTargetEnvironmentRequest,
                                     @NotNull PythonExecution pythonExecution) {
            PythonScriptExecution delegateExecution = createCopiedPythonScriptExecution(pythonExecution);
            delegateExecution.setPythonScriptPath(TargetEnvironmentFunctions.constant(BundleUtil.ROBOTCODE_DIR.toString()));
            List<Function<TargetEnvironment, String>> parameters = delegateExecution.getParameters();

            List<Function<TargetEnvironment, String>> additionalParameters = new ArrayList<>();
            int robotDebugPort = NetworkUtil.findAvailableSocketPort(DEBUGGER_DEFAULT_PORT);
            dapCommunicator = new RobotDebugAdapterProtocolCommunicator(robotDebugPort);

            additionalParameters.add(TargetEnvironmentFunctions.constant("debug"));
            additionalParameters.add(TargetEnvironmentFunctions.constant("--tcp"));
            additionalParameters.add(TargetEnvironmentFunctions.constant(String.valueOf(robotDebugPort)));
            if (executionMode != RobotExecutionMode.DEBUG) {
                additionalParameters.add(TargetEnvironmentFunctions.constant("--no-debug"));
            }
            if (executionMode == RobotExecutionMode.DRY_RUN) {
                additionalParameters.add(TargetEnvironmentFunctions.constant("--dryrun"));
            }
            parameters.addAll(0, additionalParameters);
            additionalParameters.clear();

            enrichWithTestArguments(configuration, argument -> additionalParameters.add(TargetEnvironmentFunctions.constant(argument)));
            parameters.addAll(additionalParameters);

            boolean testsOnlyMode = RobotOptionsProvider.getInstance(configuration.getProject()).testsOnlyMode();
            environment.putUserData(ExecutionKeys.TESTS_ONLY_MODE_KEY, testsOnlyMode);

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

    private static void enrichWithTestArguments(RobotRunConfiguration configuration, Consumer<String> argumentConsumer) {
        List<RobotRunnableUnitExecutionInfo> testCases = configuration.getTestCases();
        List<RobotRunnableUnitExecutionInfo> tasks = configuration.getTasks();
        Set<String> directories = new LinkedHashSet<>(configuration.getDirectories());

        if (!testCases.isEmpty()) {
            argumentConsumer.accept("--norpa");
        }
        if (!tasks.isEmpty()) {
            argumentConsumer.accept("--rpa");
        }
        for (RobotRunnableUnitExecutionInfo testCaseInfo : testCases) {
            argumentConsumer.accept("--test");
            argumentConsumer.accept(testCaseInfo.getUnitName());

            String file = computeFileLocation(testCaseInfo.getLocation());
            directories.add(file);
        }
        for (RobotRunnableUnitExecutionInfo taskInfo : tasks) {
            argumentConsumer.accept("--task");
            argumentConsumer.accept(taskInfo.getUnitName());

            String file = computeFileLocation(taskInfo.getLocation());
            directories.add(file);
        }
        for (String directory : directories) {
            argumentConsumer.accept(directory);
        }
    }

    private static String computeFileLocation(String location) {
        return location.replace('.', '/') + "." + RobotFeatureFileType.getInstance().getDefaultExtension();
    }

    private static RobotExecutionMode computeRobotExecutionMode(Executor executor) {
        return switch (executor.getId()) {
            case DefaultDebugExecutor.EXECUTOR_ID -> RobotExecutionMode.DEBUG;
            case RobotDryRunExecutor.EXECUTOR_ID -> RobotExecutionMode.DRY_RUN;
            default -> RobotExecutionMode.RUN;
        };
    }

    public void initRobotDebugCommunicatorProcess(@NotNull ProcessHandler processHandler) {
        RobotDebugAdapterProtocolCommunicator localDapCommunicator = dapCommunicator;
        processHandler.addProcessListener(localDapCommunicator);
        if (processHandler.isStartNotified()) {
            // Usually, startNotified would be called by the ProcessHandler itself and in reality, it is called by it. Sadly, when we're reaching this point,
            // the process is already running and the method called. Therefore, we have to emulate the call ourselves to connect to our debug server
            localDapCommunicator.startNotified(new ProcessEvent(processHandler));
        }
    }

    public RobotDebugAdapterProtocolCommunicator getDapCommunicator() {
        return dapCommunicator;
    }

    private enum RobotExecutionMode {

        RUN, DEBUG, DRY_RUN
    }
}
