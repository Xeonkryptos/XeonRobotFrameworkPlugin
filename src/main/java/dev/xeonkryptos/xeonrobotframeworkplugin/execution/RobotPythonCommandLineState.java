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
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.target.TargetEnvironment;
import com.intellij.execution.target.value.TargetEnvironmentFunctions;
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.jetbrains.python.actions.PyExecuteInConsole;
import com.jetbrains.python.actions.PyRunFileInConsoleAction;
import com.jetbrains.python.console.PyConsoleOptions;
import com.jetbrains.python.extensions.ContextAnchor;
import com.jetbrains.python.extensions.ModuleBasedContextAnchor;
import com.jetbrains.python.extensions.ProjectSdkContextAnchor;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration.RobotRunnableUnitExecutionInfo;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.dap.RobotDebugAdapterProtocolCommunicator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TaskNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TestCaseNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.BundleUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.NetworkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
            if (executionResult != null) {
                ProcessHandler processHandler = executionResult.getProcessHandler();
                initRobotDebugCommunicatorProcess(processHandler);
            }
            return executionResult;
        } catch (ExecutionException e) {
            throw e;
        } catch (Exception e) {
            MyLogger.logger.error(e);
            return null;
        }
    }

    @Override
    public @NotNull ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner<?> runner) throws ExecutionException {
        ExecutionResult executionResult = super.execute(executor, runner);
        startRobotDebugCommunicatorProcess(executionResult);
        return executionResult;
    }

    @Override
    public ExecutionResult execute(Executor executor, CommandLinePatcher... patchers) throws ExecutionException {
        ExecutionResult executionResult = super.execute(executor, patchers);
        startRobotDebugCommunicatorProcess(executionResult);
        return executionResult;
    }

    @Override
    public @Nullable ExecutionResult execute(@NotNull Executor executor) throws ExecutionException {
        ExecutionResult executionResult = super.execute(executor);
        startRobotDebugCommunicatorProcess(executionResult);
        return executionResult;
    }

    private void startRobotDebugCommunicatorProcess(@Nullable ExecutionResult executionResult) {
        if (executionResult != null) {
            ProcessHandler processHandler = executionResult.getProcessHandler();
            initRobotDebugCommunicatorProcess(processHandler);
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
        var wrappedConverter = new MyPythonScriptTargetedCommandLineBuilder(converter, runConfiguration, executionMode);
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
        if (runConfiguration.getPythonRunConfiguration().emulateTerminal() && executionResult != null && executionResult.getExecutionConsole() instanceof ConsoleView consoleView) {
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

        private MyPythonScriptTargetedCommandLineBuilder(@NotNull PythonScriptTargetedCommandLineBuilder parentBuilder, RobotRunConfiguration configuration, RobotExecutionMode executionMode) {
            this.parentBuilder = parentBuilder;
            this.configuration = configuration;
            this.executionMode = executionMode;
        }

        @NotNull
        @Override
        public PythonExecution build(@NotNull HelpersAwareTargetEnvironmentRequest helpersAwareTargetEnvironmentRequest, @NotNull PythonExecution pythonExecution) {
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
        PythonRunConfiguration pythonRunConfiguration = configuration.getPythonRunConfiguration();
        String expandedWorkingDir = PythonScriptCommandLineState.getExpandedWorkingDir(pythonRunConfiguration);
        VirtualFile expandedWorkingDirVFile = VfsUtil.findFile(Path.of(expandedWorkingDir), true);
        assert expandedWorkingDirVFile != null;

        Project project = pythonRunConfiguration.getProject();
        Sdk sdk = pythonRunConfiguration.getSdk();
        Module module = pythonRunConfiguration.getModule();
        ContextAnchor contextAnchor = module == null ? new ProjectSdkContextAnchor(project, sdk) : new ModuleBasedContextAnchor(module);

        if (!testCases.isEmpty()) {
            argumentConsumer.accept("--norpa");
        }
        if (!tasks.isEmpty()) {
            argumentConsumer.accept("--rpa");
        }
        for (RobotRunnableUnitExecutionInfo testCaseInfo : testCases) {
            String unitName = testCaseInfo.getUnitName();
            argumentConsumer.accept("--test");
            argumentConsumer.accept(testCaseInfo.getUnitName());

            String file = computeFileLocation(project, testCaseInfo, expandedWorkingDirVFile, () -> TestCaseNameIndex.find(unitName, project, contextAnchor.getScope()));
            directories.add(file);
        }
        for (RobotRunnableUnitExecutionInfo taskInfo : tasks) {
            String unitName = taskInfo.getUnitName();
            argumentConsumer.accept("--task");
            argumentConsumer.accept(unitName);

            String file = computeFileLocation(project, taskInfo, expandedWorkingDirVFile, () -> TaskNameIndex.find(unitName, project, contextAnchor.getScope()));
            directories.add(file);
        }
        for (String directory : directories) {
            argumentConsumer.accept(directory);
        }
    }

    private static String computeFileLocation(Project project,
                                              RobotRunnableUnitExecutionInfo execInfo,
                                              VirtualFile expandedWorkingDirVFile,
                                              Supplier<Collection<? extends RobotQualifiedNameOwner>> qualifiedNameOwnerSupplier) {
        Optional<VirtualFile> virtualFileStmtOptional = ReadAction.nonBlocking(() -> qualifiedNameOwnerSupplier.get()
                                                                                                               .stream()
                                                                                                               .filter(stmt -> execInfo.getFqdn().equals(stmt.getQualifiedName()))
                                                                                                               .findFirst()
                                                                                                               .map(stmt -> stmt.getContainingFile().getOriginalFile().getVirtualFile()))
                                                                  .inSmartMode(project)
                                                                  .executeSynchronously();
        return virtualFileStmtOptional.map(vfile -> VfsUtil.getCommonAncestor(expandedWorkingDirVFile, vfile))
                                      .map(vfile -> VfsUtil.getRelativePath(vfile, virtualFileStmtOptional.get(), '/'))
                                      .orElseGet(() -> execInfo.getLocation().replace('.', '/') + "." + RobotFeatureFileType.getInstance().getDefaultExtension());
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
