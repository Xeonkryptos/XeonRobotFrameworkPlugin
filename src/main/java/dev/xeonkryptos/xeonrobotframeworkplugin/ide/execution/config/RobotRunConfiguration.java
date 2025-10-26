package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config;

import com.intellij.execution.EnvFilesOptions;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.LocatableRunConfigurationOptions;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.python.run.PythonRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.RobotCommandLineState;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui.editor.RobotConfigurationFragmentedEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RobotRunConfiguration extends LocatableConfigurationBase<Element> implements EnvFilesOptions {

    @SuppressWarnings("ApplicationServiceAsStaticFinalFieldOrProperty")
    private static final String TEST_CASE_NAME = "testCase";
    @SuppressWarnings("ApplicationServiceAsStaticFinalFieldOrProperty")
    private static final String TASK_NAME = "task";
    @SuppressWarnings("ApplicationServiceAsStaticFinalFieldOrProperty")
    private static final String DIRECTORY_NAME = "directory";

    private PythonRunConfigurationExt pythonRunConfiguration;

    private List<RobotRunnableTestCaseExecutionInfo> testCases = List.of();
    private List<RobotRunnableTestCaseExecutionInfo> tasks = List.of();
    private List<String> directories = List.of();

    public RobotRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
        super(project, configurationFactory);

        pythonRunConfiguration = new PythonRunConfigurationExt(project);
        pythonRunConfiguration.setUseModuleSdk(true);
        pythonRunConfiguration.setModuleMode(true);
        pythonRunConfiguration.setScriptName("robotcode");
        pythonRunConfiguration.setEmulateTerminal(false);
    }

    public RobotRunConfiguration(Project project, ConfigurationFactory configurationFactory, PythonRunConfigurationExt pythonRunConfiguration) {
        super(project, configurationFactory);

        this.pythonRunConfiguration = pythonRunConfiguration;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RobotConfigurationFragmentedEditor(this);
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) {
        return new RobotCommandLineState(this, env);
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);

        pythonRunConfiguration.readExternal(element);

        testCases = deserializeList(element, TEST_CASE_NAME);
        tasks = deserializeList(element, TASK_NAME);
        directories = JDOMExternalizerUtil.getChildrenValueAttributes(element, DIRECTORY_NAME);
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);

        pythonRunConfiguration.writeExternal(element);

        serializeList(element, testCases, TEST_CASE_NAME);
        serializeList(element, tasks, TASK_NAME);
        JDOMExternalizerUtil.addChildrenWithValueAttribute(element, DIRECTORY_NAME, directories);
    }

    @NotNull
    private List<RobotRunnableTestCaseExecutionInfo> deserializeList(Element parent, String name) {
        return ContainerUtil.mapNotNull(parent.getChildren(name), e -> {
            RobotRunnableTestCaseExecutionInfo info = new RobotRunnableTestCaseExecutionInfo();
            info.readExternal(e);
            return info;
        });
    }

    private void serializeList(Element parent, List<RobotRunnableTestCaseExecutionInfo> list, String name) throws WriteExternalException {
        for (RobotRunnableTestCaseExecutionInfo item : list) {
            if (item != null) {
                Element itemElement = new Element(name);
                item.writeExternal(itemElement);
                parent.addContent(itemElement);
            }
        }
    }

    @NotNull
    @Override
    public List<String> getEnvFilePaths() {
        return pythonRunConfiguration.getEnvFilePaths();
    }

    @Override
    public void setEnvFilePaths(@NotNull List<String> list) {
        pythonRunConfiguration.setEnvFilePaths(list);
    }

    public PythonRunConfiguration getPythonRunConfiguration() {
        return pythonRunConfiguration;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();
        pythonRunConfiguration.checkConfiguration();
    }

    @Override
    public RobotRunConfiguration clone() {
        RobotRunConfiguration config = (RobotRunConfiguration) super.clone();
        config.pythonRunConfiguration = pythonRunConfiguration.clone();
        return config;
    }

    @NotNull
    @Override
    protected LocatableRunConfigurationOptions getOptions() {
        return pythonRunConfiguration.getOptions();
    }

    public List<RobotRunnableTestCaseExecutionInfo> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<RobotRunnableTestCaseExecutionInfo> testCases) {
        this.testCases = Objects.requireNonNullElseGet(testCases, List::of);
    }

    public List<RobotRunnableTestCaseExecutionInfo> getTasks() {
        return tasks;
    }

    public void setTasks(List<RobotRunnableTestCaseExecutionInfo> tasks) {
        this.tasks = Objects.requireNonNullElseGet(tasks, List::of);
    }

    public List<String> getDirectories() {
        return directories;
    }

    public void setDirectories(List<String> directories) {
        this.directories = Objects.requireNonNullElseGet(directories, List::of);
    }

    public static class RobotRunnableTestCaseExecutionInfo {

        private String directory;
        private String unitName;

        protected RobotRunnableTestCaseExecutionInfo() {}

        public RobotRunnableTestCaseExecutionInfo(String fqdn) {
            int unitNameStartIndex = fqdn.lastIndexOf('.');
            if (unitNameStartIndex == -1) {
                directory = "";
                unitName = fqdn;
            } else {
                directory = fqdn.substring(0, unitNameStartIndex);
                unitName = fqdn.substring(unitNameStartIndex + 1);
            }
        }

        public void readExternal(Element element) throws InvalidDataException {
            directory = JDOMExternalizerUtil.readField(element, "directory");
            unitName = JDOMExternalizerUtil.readField(element, "testCaseName");
        }

        public void writeExternal(Element element) throws WriteExternalException {
            JDOMExternalizerUtil.writeField(element, "directory", directory);
            JDOMExternalizerUtil.writeField(element, "testCaseName", unitName);
        }
    }
}
