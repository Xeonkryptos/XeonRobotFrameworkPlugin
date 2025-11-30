package dev.xeonkryptos.xeonrobotframeworkplugin.execution;

import com.intellij.execution.testframework.sm.runner.ui.TestStackTraceParser;

public class RobotTestStackTraceParser extends TestStackTraceParser {

    public RobotTestStackTraceParser() {
        this(-1, null, null, null);
    }

    public RobotTestStackTraceParser(int failedLine, String failedMethodName, String errorMessage, String topLocationLine) {
        super(failedLine, failedMethodName, errorMessage, topLocationLine);
    }
}
