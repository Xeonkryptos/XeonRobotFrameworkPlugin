package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonScriptCommandLineState;
import org.jetbrains.annotations.NotNull;

public class RobotRunConfiguration extends PythonRunConfiguration {

    public RobotRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
        super(project, configurationFactory);

        setEmulateTerminal(true);
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        PythonScriptCommandLineState state = (PythonScriptCommandLineState) super.getState(executor, env);
        assert state != null;
        state.setMultiprocessDebug(true);
        return state;
    }
}
