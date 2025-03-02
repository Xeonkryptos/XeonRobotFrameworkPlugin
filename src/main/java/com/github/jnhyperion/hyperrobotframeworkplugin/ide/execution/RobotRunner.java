package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.configurations.RunProfile;
import com.jetbrains.python.run.PythonRunner;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class RobotRunner extends PythonRunner {

    @NonNls
    @NotNull
    @Override
    public String getRunnerId() {
        return "com.github.jnhyperion.hyperrobotframeworkplugin.HyperRobotFrameworkRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return (super.canRun(executorId, profile) || RobotDryRunExecutor.EXECUTOR_ID.equals(executorId)) && profile instanceof RobotRunConfiguration;
    }
}
