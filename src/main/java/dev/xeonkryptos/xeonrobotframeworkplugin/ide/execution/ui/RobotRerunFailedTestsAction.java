package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui;

import com.intellij.execution.Executor;
import com.intellij.execution.Location;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestFrameworkRunningModel;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.vfs.VirtualFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.RobotCommandLineState;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.FileUtils;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.PythonRunConfigurationExt;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotConfigurationFactory;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfigurationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotRerunFailedTestsAction extends AbstractRerunFailedTestsAction {

    public RobotRerunFailedTestsAction(@NotNull ComponentContainer componentContainer) {
        super(componentContainer);
    }

    @Nullable
    @Override
    protected MyRunProfile getRunProfile(@NotNull ExecutionEnvironment environment) {
        TestFrameworkRunningModel model = getModel();
        if (model == null) {
            return null;
        }
        return new MyTestRunProfile((PythonRunConfigurationExt) model.getProperties().getConfiguration());
    }

    private class MyTestRunProfile extends MyRunProfile {

        public MyTestRunProfile(@NotNull RunConfigurationBase<?> configuration) {
            super((RunConfigurationBase<?>) configuration.clone());
        }

        @Nullable
        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
            List<AbstractTestProxy> failedTests = getFailedTests(environment.getProject());
            Set<String> testNames = new LinkedHashSet<>();
            Set<VirtualFile> testFiles = new LinkedHashSet<>();
            for (AbstractTestProxy failedTest : failedTests) {
                if (failedTest.isLeaf()) {
                    AbstractTestProxy failedKeywordTest = failedTest.getParent();
                    Location<?> location = failedKeywordTest.getLocation(getProject(), myConsoleProperties.getScope());
                    testNames.add(failedKeywordTest.getName());
                    if (location != null && location.getVirtualFile() != null) {
                        testFiles.add(location.getVirtualFile());
                    }
                }
            }
            if (testNames.isEmpty() || testFiles.isEmpty()) {
                return null;
            }

            RobotConfigurationFactory configurationFactory = RobotRunConfigurationType.getRobotRunConfigurationType().getConfigurationFactory();
            RobotRunConfiguration robotRunConfiguration = new RobotRunConfiguration(getProject(), configurationFactory, getPeer());
            String workingDirectoryToUse = FileUtils.getWorkingDirectoryToUse(robotRunConfiguration);

            String testExecutionCommand = testNames.stream().collect(Collectors.joining("\" --test \"", "--test \"", "\""));
            testExecutionCommand += " " + testFiles.stream()
                                                   .map(VirtualFile::getPath)
                                                   .map(path -> FileUtils.relativizePath(workingDirectoryToUse, path))
                                                   .collect(Collectors.joining("\" \"", "\"", "\""));
            robotRunConfiguration.getPythonRunConfiguration().setScriptParameters(testExecutionCommand);

            return new RobotCommandLineState(robotRunConfiguration, environment);
        }

        @NotNull
        @Override
        public PythonRunConfigurationExt getPeer() {
            return (PythonRunConfigurationExt) super.getPeer();
        }
    }
}
