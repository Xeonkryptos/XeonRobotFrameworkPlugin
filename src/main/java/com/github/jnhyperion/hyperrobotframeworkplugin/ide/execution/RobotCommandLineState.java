package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.config.RobotRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

public class RobotCommandLineState extends CommandLineState {

    private final RobotRunConfiguration robotRunConfiguration;

    public RobotCommandLineState(RobotRunConfiguration robotRunConfiguration, @NotNull ExecutionEnvironment env) {
        super(env);

        this.robotRunConfiguration = robotRunConfiguration;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        return new RobotPythonCommandLineState(robotRunConfiguration, getEnvironment()).startProcess();
    }

    public RobotRunConfiguration getRobotRunConfiguration() {
        return robotRunConfiguration;
    }
}
