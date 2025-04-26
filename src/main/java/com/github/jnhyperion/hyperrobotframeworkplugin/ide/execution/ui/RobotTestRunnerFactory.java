package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.ui;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.config.RobotRunConfiguration;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.ui.ConsoleView;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jetbrains.annotations.NotNull;

public class RobotTestRunnerFactory {

    public static ConsoleView createConsoleView(@NotNull RunProfile runConfiguration, @NotNull Executor executor) {
        if (!(runConfiguration instanceof RobotRunConfiguration) && !(runConfiguration instanceof PythonRunConfiguration)) {
            throw new IllegalArgumentException("Expected RobotRunConfiguration, got " + runConfiguration.getClass().getName());
        }

        RobotConsoleProperties consoleProperties = new RobotConsoleProperties((RunConfiguration) runConfiguration, executor);

        // Use the built-in SMTestRunnerConnectionUtil to create the console
        // This util creates a properly configured test runner console with all needed listeners
        return SMTestRunnerConnectionUtil.createConsole("RobotTestRunner", consoleProperties);
    }
}
