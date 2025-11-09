package dev.xeonkryptos.xeonrobotframeworkplugin.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.SMRunnerConsolePropertiesProvider;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.ui.ConsoleView;
import com.jetbrains.python.run.PythonRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration;
import org.jetbrains.annotations.NotNull;

public class RobotTestRunnerFactory {

    public static ConsoleView createConsoleView(@NotNull RunProfile runConfiguration,
                                                @NotNull Executor executor,
                                                RobotPythonCommandLineState robotPythonCommandLineState) {
        if (!(runConfiguration instanceof RobotRunConfiguration) && !(runConfiguration instanceof PythonRunConfiguration)) {
            throw new IllegalArgumentException("Expected RobotRunConfiguration, got " + runConfiguration.getClass().getName());
        }

        if (runConfiguration instanceof SMRunnerConsolePropertiesProvider provider) {
            SMTRunnerConsoleProperties testConsoleProperties = provider.createTestConsoleProperties(executor);
            if (testConsoleProperties instanceof RobotConsoleProperties robotConsoleProperties) {
                robotConsoleProperties.setState(robotPythonCommandLineState);
            }
            return SMTestRunnerConnectionUtil.createConsole("RobotTestRunner", testConsoleProperties);
        }

        RobotConsoleProperties consoleProperties = new RobotConsoleProperties((RunConfiguration) runConfiguration, executor);
        // Use the built-in SMTestRunnerConnectionUtil to create the console
        // This util creates a properly configured test runner console with all needed listeners
        return SMTestRunnerConnectionUtil.createConsole("RobotTestRunner", consoleProperties);
    }
}
