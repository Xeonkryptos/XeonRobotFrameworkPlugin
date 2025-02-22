package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.ParamsGroup;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.TargetEnvironment;
import com.intellij.execution.target.value.TargetEnvironmentFunctions;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.Key;
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
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class RobotPythonScriptCommandLineState extends PythonScriptCommandLineState {

    public static final Key<Integer> ROBOT_DEBUG_PORT = Key.create("ROBOT_DEBUG_PORT");

    private static final Path DATA_DIR = PathManager.getPluginsDir().resolve("Hyper RobotFramework Support").resolve("data");
    private static final Path BUNDLED_DIR = DATA_DIR.resolve("bundled");
    private static final Path DEBUG_DIR = BUNDLED_DIR.resolve("debugging");

    private final RobotRunConfiguration robotRunConfiguration;

    public RobotPythonScriptCommandLineState(RobotRunConfiguration robotRunConfiguration, @NotNull ExecutionEnvironment env) {
        super(robotRunConfiguration, env);

        this.robotRunConfiguration = robotRunConfiguration;
    }

    @Override
    public @Nullable ExecutionResult execute(Executor executor, PythonProcessStarter processStarter, CommandLinePatcher... patchers) throws ExecutionException {
        return super.execute(executor, processStarter, ArrayUtil.append(patchers, commandLine -> {
            ParametersList parametersList = commandLine.getParametersList();
            String modeCommand = "--module";
            ParamsGroup paramsGroup = parametersList.getParamsGroup(PythonCommandLineState.GROUP_MODULE);
            if (paramsGroup == null) {
                paramsGroup = parametersList.getParamsGroup(PythonCommandLineState.GROUP_SCRIPT);
                modeCommand = "--script";
            }
            if (paramsGroup != null) {
                int robotDebugPort = findAvailableSocketPort();
                robotRunConfiguration.putUserData(ROBOT_DEBUG_PORT, robotDebugPort);

                int parameterIndex = 0;
                paramsGroup.getParametersList().addAt(parameterIndex, modeCommand);
                parameterIndex++;
                paramsGroup.getParametersList().addAt(parameterIndex, "--listen");
                parameterIndex++;
                paramsGroup.getParametersList().addAt(parameterIndex, String.valueOf(robotDebugPort));
            }
        }));
    }

    @Nullable
    @Override
    @SuppressWarnings("UnstableApiUsage") // Might be unstable at the moment, but is an important extension point
    public ExecutionResult execute(@NotNull Executor executor, @NotNull PythonScriptTargetedCommandLineBuilder converter) throws ExecutionException {
        return super.execute(executor, new MyPythonScriptTargetedCommandLineBuilder(converter, robotRunConfiguration));
    }

    @SuppressWarnings("UnstableApiUsage") // Might be unstable at the moment, but is an important extension point
    private record MyPythonScriptTargetedCommandLineBuilder(@NotNull PythonScriptTargetedCommandLineBuilder parentBuilder, RobotRunConfiguration configuration)
            implements PythonScriptTargetedCommandLineBuilder {

        @Override
        public @NotNull PythonExecution build(@NotNull HelpersAwareTargetEnvironmentRequest helpersAwareTargetEnvironmentRequest,
                                              @NotNull PythonExecution pythonExecution) {
            int robotDebugPort = findAvailableSocketPort();
            configuration.putUserData(ROBOT_DEBUG_PORT, robotDebugPort);

            PythonScriptExecution delegateExecution = createCopiedPythonScriptExecution(pythonExecution);
            // TODO: Rename script to the real script name when it is defined
            String robotDebugScriptLocation = DEBUG_DIR.resolve("robot-debug-script.py").toAbsolutePath().toString();
            delegateExecution.setPythonScriptPath(TargetEnvironmentFunctions.constant(robotDebugScriptLocation));

            PythonExecution.Visitor visitor = new PythonExecution.Visitor() {
                @Override
                public void visit(@NotNull PythonModuleExecution pythonModuleExecution) {
                    String moduleName = pythonModuleExecution.getModuleName();
                    if (moduleName == null) {
                        throw new IllegalArgumentException("Python module name must be set");
                    }
                    delegateExecution.getParameters()
                                     .addAll(0, List.of(TargetEnvironmentFunctions.constant("--module"), TargetEnvironmentFunctions.constant(moduleName)));
                }

                @Override
                public void visit(@NotNull PythonScriptExecution pythonScriptExecution) {
                    Function<TargetEnvironment, String> pythonScriptPath = pythonScriptExecution.getPythonScriptPath();
                    if (pythonScriptPath == null) {
                        throw new IllegalArgumentException("Python script path must be set");
                    }
                    delegateExecution.getParameters().addAll(0, List.of(TargetEnvironmentFunctions.constant("--script"), pythonScriptPath));
                }
            };
            pythonExecution.accept(visitor);
            delegateExecution.getParameters()
                             .addAll(0,
                                     List.of(TargetEnvironmentFunctions.constant("--listen"),
                                             TargetEnvironmentFunctions.constant(String.valueOf(robotDebugPort))));

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

    private static int findAvailableSocketPort() {
        try {
            return NetUtils.findAvailableSocketPort();
        } catch (IOException e) {
            throw new RuntimeException(PyBundle.message("runcfg.error.message.failed.to.find.free.socket.port"), e);
        }
    }

    public Integer getRobotDebugPort() {
        return robotRunConfiguration.getUserData(ROBOT_DEBUG_PORT);
    }
}
