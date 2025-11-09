package dev.xeonkryptos.xeonrobotframeworkplugin.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import org.jetbrains.annotations.NotNull;

public class RobotConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {

    private volatile RobotPythonCommandLineState state;

    public RobotConsoleProperties(@NotNull RunConfiguration config, @NotNull Executor executor) {
        super(config, "Robot Framework", executor);

        setIdBasedTestTree(true);
        setPrintTestingStartedTime(false);
        setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false);
        setIfUndefined(TestConsoleProperties.HIDE_IGNORED_TEST, false);
        setIfUndefined(TestConsoleProperties.SCROLL_TO_SOURCE, true);
        setIfUndefined(TestConsoleProperties.SELECT_FIRST_DEFECT, true);
        setIfUndefined(TestConsoleProperties.TRACK_RUNNING_TEST, true);
    }

    @Override
    public SMTestLocator getTestLocator() {
        return new RobotSMTestLocator();
    }

    @Override
    public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        if (consoleProperties instanceof RobotConsoleProperties robotConsoleProperties) {
            return new RobotOutputToGeneralTestEventsConverter(testFrameworkName, robotConsoleProperties);
        }
        return new OutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties);
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    public RobotPythonCommandLineState getState() {
        return state;
    }

    void setState(RobotPythonCommandLineState state) {
        this.state = state;
    }
}
