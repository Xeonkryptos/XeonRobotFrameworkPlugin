package dev.xeonkryptos.xeonrobotframeworkplugin.execution;

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
import com.intellij.psi.PsiFile;
import com.jetbrains.python.extensions.ContextAnchor;
import com.jetbrains.python.extensions.ModuleBasedContextAnchor;
import com.jetbrains.python.extensions.ProjectSdkContextAnchor;
import com.jetbrains.python.run.PythonRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration.RobotRunnableUnitExecutionInfo;
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
        return new MyTestRunProfile((RobotRunConfiguration) model.getProperties().getConfiguration());
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
            Project project = getProject();
            for (AbstractTestProxy failedTest : failedTests) {
                if (failedTest.isLeaf()) {
                    Location<?> location = failedTest.getLocation(project, myConsoleProperties.getScope());
                    testNames.add(failedTest.getName());
                    if (location != null && location.getVirtualFile() != null) {
                        testFiles.add(location.getVirtualFile());
                    }
                }
            }
            if (testNames.isEmpty() || testFiles.isEmpty()) {
                return null;
            }

            RobotRunConfiguration robotRunConfiguration = getPeer();
            PythonRunConfiguration pythonRunConfiguration = robotRunConfiguration.getPythonRunConfiguration();
            List<RobotRunnableUnitExecutionInfo> testCaseInfos = new ArrayList<>();
            robotRunConfiguration.setTestCases(testCaseInfos);

            Sdk sdk = pythonRunConfiguration.getSdk();
            Module module = pythonRunConfiguration.getModule();
            ContextAnchor contextAnchor = module == null ? new ProjectSdkContextAnchor(project, sdk) : new ModuleBasedContextAnchor(module);
            for (String testName : testNames) {
                for (RobotTestCaseStatement statement : TestCaseNameIndex.find(testName, project, contextAnchor.getScope())) {
                    PsiFile containingFile = statement.getContainingFile();
                    VirtualFile virtualFile = containingFile.getVirtualFile();
                    if (virtualFile == null) {
                        virtualFile = containingFile.getOriginalFile().getVirtualFile();
                    }
                    if (testFiles.contains(virtualFile)) {
                        String qualifiedName = statement.getQualifiedName();
                        String qualifiedLocation = qualifiedName.substring(0, qualifiedName.length() - testName.length() - 1);
                        RobotRunnableUnitExecutionInfo testCaseInfo = new RobotRunnableUnitExecutionInfo(qualifiedLocation, testName);
                        testCaseInfos.add(testCaseInfo);
                        break;
                    }
                }
            }
            return new RobotCommandLineState(robotRunConfiguration, environment);
        }

        @NotNull
        @Override
        public RobotRunConfiguration getPeer() {
            return (RobotRunConfiguration) super.getPeer();
        }
    }
}
