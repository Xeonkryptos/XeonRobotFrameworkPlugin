package dev.xeonkryptos.xeonrobotframeworkplugin.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.Location;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.execution.testframework.sm.runner.ui.TestStackTraceParser;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
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
    public TestStackTraceParser getTestStackTraceParser(@NotNull String url, @NotNull SMTestProxy proxy, @NotNull Project project) {
        if (proxy.getErrorMessage() != null) {
            int failedLine = -1;
            Location<?> location = proxy.getLocation(project, GlobalSearchScope.projectScope(project));
            if (location != null) {
                PsiElement psiElement = location.getPsiElement();
                PsiFile containingFile = psiElement.getContainingFile();
                Document document = PsiDocumentManager.getInstance(project).getDocument(containingFile);
                if (document != null) {
                    failedLine = document.getLineNumber(psiElement.getTextOffset());
                }
            }
            String name = proxy.getName();
            String errorMessage = proxy.getErrorMessage();
            return new RobotTestStackTraceParser(failedLine, name, errorMessage, null);
        }
        return new RobotTestStackTraceParser();
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
