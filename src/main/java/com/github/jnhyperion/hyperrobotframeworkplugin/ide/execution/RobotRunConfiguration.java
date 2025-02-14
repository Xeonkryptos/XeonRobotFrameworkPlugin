package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.ParamsGroup;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.TargetEnvironment;
import com.intellij.execution.target.value.TargetEnvironmentFunctions;
import com.intellij.openapi.project.Project;
import com.intellij.util.ArrayUtil;
import com.jetbrains.python.run.CommandLinePatcher;
import com.jetbrains.python.run.PythonCommandLineState;
import com.jetbrains.python.run.PythonExecution;
import com.jetbrains.python.run.PythonModuleExecution;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonScriptCommandLineState;
import com.jetbrains.python.run.PythonScriptExecution;
import com.jetbrains.python.run.PythonScriptTargetedCommandLineBuilder;
import com.jetbrains.python.run.target.HelpersAwareTargetEnvironmentRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;

public class RobotRunConfiguration extends PythonRunConfiguration {

    public RobotRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
        super(project, configurationFactory);

        setEmulateTerminal(true);
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) {
        PythonScriptCommandLineState state = new PythonScriptCommandLineState(this, env) {
            @Override
            public @Nullable ExecutionResult execute(Executor executor, PythonProcessStarter processStarter, CommandLinePatcher... patchers) throws
                                                                                                                                             ExecutionException {
                return super.execute(executor, processStarter, ArrayUtil.append(patchers, commandLine -> {
                    ParametersList parametersList = commandLine.getParametersList();
                    String modeCommand = "--module";
                    ParamsGroup paramsGroup = parametersList.getParamsGroup(PythonCommandLineState.GROUP_MODULE);
                    if (paramsGroup == null) {
                        paramsGroup = parametersList.getParamsGroup(PythonCommandLineState.GROUP_SCRIPT);
                        modeCommand = "--script";
                    }
                    if (paramsGroup != null) {
                        int parameterIndex = 0;
                        paramsGroup.getParametersList().addAt(parameterIndex, modeCommand);
                        parameterIndex++;
                        paramsGroup.getParametersList().addAt(parameterIndex, "--listen");
                        parameterIndex++;
                        paramsGroup.getParametersList().addAt(parameterIndex, "9999");
                    }
                }));
            }

            @Override
            @SuppressWarnings("UnstableApiUsage") // Might be unstable at the moment, but is an important extension point
            public @Nullable ExecutionResult execute(@NotNull Executor executor, @NotNull PythonScriptTargetedCommandLineBuilder converter) throws
                                                                                                                                            ExecutionException {
                return super.execute(executor, new MyPythonScriptTargetedCommandLineBuilder(converter));
            }
        };
        state.setMultiprocessDebug(true);
        return state;
    }

    @SuppressWarnings("UnstableApiUsage") // Might be unstable at the moment, but is an important extension point
    private record MyPythonScriptTargetedCommandLineBuilder(PythonScriptTargetedCommandLineBuilder parentBuilder)
            implements PythonScriptTargetedCommandLineBuilder {

        private MyPythonScriptTargetedCommandLineBuilder(@NotNull PythonScriptTargetedCommandLineBuilder parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        @Override
        public @NotNull PythonExecution build(@NotNull HelpersAwareTargetEnvironmentRequest helpersAwareTargetEnvironmentRequest,
                                              @NotNull PythonExecution pythonExecution) {
            PythonScriptExecution delegateExecution = createCopiedPythonScriptExecution(pythonExecution);
            delegateExecution.setPythonScriptPath(TargetEnvironmentFunctions.constant("robot-debug-script.py"));

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
            delegateExecution.getParameters().addAll(0, List.of(TargetEnvironmentFunctions.constant("--listen"), TargetEnvironmentFunctions.constant("9999")));

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
}
