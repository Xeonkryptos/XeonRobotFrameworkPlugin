package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui;

import com.intellij.execution.Executor;
import com.intellij.execution.Location;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestFrameworkRunningModel;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.extensions.ContextAnchor;
import com.jetbrains.python.extensions.ModuleBasedContextAnchor;
import com.jetbrains.python.extensions.ProjectSdkContextAnchor;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ExecutionKeys;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.RobotCommandLineState;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.PythonRunConfigurationExt;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotConfigurationFactory;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration.RobotRunnableUnitExecutionInfo;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfigurationType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TestCaseNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
            Boolean testsOnlyMode = environment.getUserData(ExecutionKeys.TESTS_ONLY_MODE_KEY);
            Project project = getProject();
            for (AbstractTestProxy failedTest : failedTests) {
                if (failedTest.isLeaf()) {
                    AbstractTestProxy failedKeywordTest;
                    if (testsOnlyMode) {
                        failedKeywordTest = failedTest;
                    } else {
                        failedKeywordTest = failedTest.getParent();
                    }
                    Location<?> location = failedKeywordTest.getLocation(project, myConsoleProperties.getScope());
                    testNames.add(failedKeywordTest.getName());
                    if (location != null && location.getVirtualFile() != null) {
                        testFiles.add(location.getVirtualFile());
                    }
                }
            }
            if (testNames.isEmpty() || testFiles.isEmpty()) {
                return null;
            }

            PythonRunConfigurationExt peer = getPeer();
            RobotConfigurationFactory configurationFactory = RobotRunConfigurationType.getRobotRunConfigurationType().getConfigurationFactory();
            RobotRunConfiguration robotRunConfiguration = new RobotRunConfiguration(project, configurationFactory, peer);

            List<RobotRunnableUnitExecutionInfo> testCaseInfos = new ArrayList<>();
            robotRunConfiguration.setTestCases(testCaseInfos);

            Sdk sdk = peer.getSdk();
            Module module = peer.getModule();
            ContextAnchor contextAnchor = module == null ? new ProjectSdkContextAnchor(project, sdk) : new ModuleBasedContextAnchor(module);
            for (String testName : testNames) {
                for (RobotTestCaseStatement statement : TestCaseNameIndex.find(testName, project, contextAnchor.getScope())) {
                    VirtualFile virtualFile = statement.getContainingFile().getViewProvider().getVirtualFile();
                    if (testFiles.contains(virtualFile)) {
                        String qualifiedName = statement.getQualifiedName();
                        RobotRunnableUnitExecutionInfo testCaseInfo = new RobotRunnableUnitExecutionInfo(qualifiedName);
                        testCaseInfos.add(testCaseInfo);
                        break;
                    }
                }
            }
            return new RobotCommandLineState(robotRunConfiguration, environment);
        }

        @NotNull
        @Override
        public PythonRunConfigurationExt getPeer() {
            return (PythonRunConfigurationExt) super.getPeer();
        }
    }
}
