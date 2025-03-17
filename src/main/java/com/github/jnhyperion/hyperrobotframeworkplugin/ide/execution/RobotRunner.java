package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.AsyncProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.jetbrains.python.run.PythonRunner;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

public class RobotRunner extends AsyncProgramRunner<RunnerSettings> {

    @NonNls
    @NotNull
    @Override
    public String getRunnerId() {
        return "com.github.jnhyperion.hyperrobotframeworkplugin.HyperRobotFrameworkRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return RobotDryRunExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof RobotRunConfiguration;
    }

    @NotNull
    @Override
    protected Promise<RunContentDescriptor> execute(@NotNull ExecutionEnvironment executionEnvironment, @NotNull RunProfileState runProfileState) {
        return new CustomPythonRunner().execute(executionEnvironment, runProfileState);
    }

    private static class CustomPythonRunner extends PythonRunner {

        @Override
        public @NotNull Promise<@Nullable RunContentDescriptor> execute(@NotNull ExecutionEnvironment env, @NotNull RunProfileState state) {
            return super.execute(env, state);
        }
    }
}
