package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import org.jetbrains.annotations.NotNull;

public class RobotConsoleProperties extends SMTRunnerConsoleProperties {
    
    public RobotConsoleProperties(@NotNull RunConfiguration config, @NotNull Executor executor) {
        super(config, "RobotFramework", executor);
        
        // Enable default test tree features
        setIdBasedTestTree(true);
        setPrintTestingStartedTime(false);
        setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false);
        setIfUndefined(TestConsoleProperties.HIDE_IGNORED_TEST, false);
        setIfUndefined(TestConsoleProperties.SCROLL_TO_SOURCE, true);
        setIfUndefined(TestConsoleProperties.SELECT_FIRST_DEFECT, true);
        setIfUndefined(TestConsoleProperties.TRACK_RUNNING_TEST, true);
    }
}
