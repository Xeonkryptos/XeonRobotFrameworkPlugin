package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.Filter;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.testframework.sm.runner.ui.TestResultsViewer;
import com.intellij.execution.testframework.sm.runner.ui.TestResultsViewer.EventsListener;
import com.intellij.execution.ui.ConsoleView;
import com.jetbrains.python.run.PythonRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RobotTestRunnerFactory {

    @SuppressWarnings("unchecked")
    private static final Filter<SMTestProxy> KEYWORD_SUITE_FILTER = (Filter<SMTestProxy>) Filter.LEAF.and(new Filter<>() {
        @Override
        public boolean shouldAccept(AbstractTestProxy test) {
            return test.getMetainfo() != null;
        }
    });

    public static ConsoleView createConsoleView(@NotNull RunProfile runConfiguration, @NotNull Executor executor) {
        if (!(runConfiguration instanceof RobotRunConfiguration) && !(runConfiguration instanceof PythonRunConfiguration)) {
            throw new IllegalArgumentException("Expected RobotRunConfiguration, got " + runConfiguration.getClass().getName());
        }

        RobotConsoleProperties consoleProperties = new RobotConsoleProperties((RunConfiguration) runConfiguration, executor);

        // Use the built-in SMTestRunnerConnectionUtil to create the console
        // This util creates a properly configured test runner console with all needed listeners
        SMTRunnerConsoleView smtConsoleView = (SMTRunnerConsoleView) SMTestRunnerConnectionUtil.createConsole("RobotTestRunner", consoleProperties);
        smtConsoleView.getResultsViewer().addEventsListener(new EventsListener() {
            @Override
            public void onTestingFinished(@NotNull TestResultsViewer sender) {
                List<SMTestProxy> allTests = sender.getTestsRootNode().getAllTests();
                for (SMTestProxy smTestProxy : KEYWORD_SUITE_FILTER.select(allTests)) {
                    String metainfo = smTestProxy.getMetainfo();
                    assert metainfo != null;

                    String[] metainfoParts = metainfo.split(";");
                    for (String metainfoPart : metainfoParts) {
                        String[] keyValuePair = metainfoPart.split("=");
                        if ("status".equals(keyValuePair[0])) {
                            String status = keyValuePair[1];
                            switch (status) {
                                case "FAIL" -> smTestProxy.setTestFailed(null, null, false);
                                case "SKIP", "NOT RUN" -> smTestProxy.setTestIgnored(null, null);
                            }
                        }
                    }
                }
            }
        });
        return smtConsoleView;
    }
}
