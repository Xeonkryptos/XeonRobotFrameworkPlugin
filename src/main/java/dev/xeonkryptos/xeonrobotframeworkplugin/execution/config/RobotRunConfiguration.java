package dev.xeonkryptos.xeonrobotframeworkplugin.execution.config;

import com.intellij.execution.EnvFilesOptions;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.LocatableRunConfigurationOptions;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.sm.runner.SMRunnerConsolePropertiesProvider;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.python.run.PythonRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.RobotCommandLineState;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.RobotConsoleProperties;
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.ui.editor.RobotConfigurationFragmentedEditor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TaskNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TestCaseNameIndex;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class RobotRunConfiguration extends LocatableConfigurationBase<Element> implements EnvFilesOptions, SMRunnerConsolePropertiesProvider {

    @SuppressWarnings("ApplicationServiceAsStaticFinalFieldOrProperty")
    private static final String TEST_CASE_NAME = "testCase";
    @SuppressWarnings("ApplicationServiceAsStaticFinalFieldOrProperty")
    private static final String TASK_NAME = "task";
    @SuppressWarnings("ApplicationServiceAsStaticFinalFieldOrProperty")
    private static final String DIRECTORY_NAME = "directory";

    private PythonRunConfigurationExt pythonRunConfiguration;

    private List<RobotRunnableUnitExecutionInfo> testCases = List.of();
    private List<RobotRunnableUnitExecutionInfo> tasks = List.of();
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
    private List<RobotRunnableUnitExecutionInfo> deserializeList(Element parent, String name) {
        return ContainerUtil.mapNotNull(parent.getChildren(name), e -> {
            RobotRunnableUnitExecutionInfo info = new RobotRunnableUnitExecutionInfo();
            info.readExternal(e);
            return info;
        });
    }

    private void serializeList(Element parent, List<RobotRunnableUnitExecutionInfo> list, String name) throws WriteExternalException {
        for (RobotRunnableUnitExecutionInfo item : list) {
            if (item != null) {
                Element itemElement = new Element(name);
                item.writeExternal(itemElement);
                parent.addContent(itemElement);
            }
        }
    }

    @NotNull
    @Override
    public SMTRunnerConsoleProperties createTestConsoleProperties(@NotNull Executor executor) {
        return new RobotConsoleProperties(this, executor);
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
        Project project = getProject();
        GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

        Module module = pythonRunConfiguration.getModule();
        if (module == null) {
            throw new RuntimeConfigurationError(RobotBundle.message("robot.run.configuration.error.module.not.specified"));
        }

        long countOfResolvableTestCases = getTestCases().stream().filter(execInfo -> {
            String fqdn = execInfo.getFqdn();
            String unitName = execInfo.getUnitName();
            return TestCaseNameIndex.find(unitName, project, allScope).stream().anyMatch(testCase -> fqdn.equals(testCase.getQualifiedName()));
        }).count();
        long countOfResolvableTasks = getTasks().stream().filter(execInfo -> {
            String fqdn = execInfo.getFqdn();
            String unitName = execInfo.getUnitName();
            return TaskNameIndex.find(unitName, project, allScope).stream().anyMatch(task -> fqdn.equals(task.getQualifiedName()));
        }).count();

        int expectedTestCasesCount = getTestCases().size();
        int expectedTasksCount = getTasks().size();
        if (expectedTestCasesCount != countOfResolvableTestCases || expectedTasksCount != countOfResolvableTasks) {
            throw new RuntimeConfigurationError(RobotBundle.message("robot.run.configuration.error.missing.testcases.or.tasks"));
        }

        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        long countOfResolvableDirectories = getDirectories().stream()
                                                            .filter(dir -> doesFileOrDirectoryExistInRoots(contentRoots, dir)
                                                                           || VfsUtil.findFile(Path.of(dir), false) != null)
                                                            .count();
        int expectedDirectoriesCount = getDirectories().size();
        if (expectedDirectoriesCount != countOfResolvableDirectories) {
            throw new RuntimeConfigurationWarning(RobotBundle.message("robot.run.configuration.warning.missing.files_directories"));
        }
    }

    private static boolean doesFileOrDirectoryExistInRoots(VirtualFile[] roots, String path) {
        String[] pathParts = path.split("[\\\\/]");
        for (VirtualFile root : roots) {
            VirtualFile file = VfsUtil.findRelativeFile(root, pathParts);
            if (file != null) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    protected LocatableRunConfigurationOptions getOptions() {
        return pythonRunConfiguration.getOptions();
    }

    public List<RobotRunnableUnitExecutionInfo> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<RobotRunnableUnitExecutionInfo> testCases) {
        this.testCases = Objects.requireNonNullElseGet(testCases, List::of);
    }

    public List<RobotRunnableUnitExecutionInfo> getTasks() {
        return tasks;
    }

    public void setTasks(List<RobotRunnableUnitExecutionInfo> tasks) {
        this.tasks = Objects.requireNonNullElseGet(tasks, List::of);
    }

    public List<String> getDirectories() {
        return directories;
    }

    public void setDirectories(List<String> directories) {
        this.directories = Objects.requireNonNullElseGet(directories, List::of);
    }

    @Override
    public RobotRunConfiguration clone() {
        RobotRunConfiguration config = (RobotRunConfiguration) super.clone();
        config.pythonRunConfiguration = pythonRunConfiguration.clone();
        config.setTestCases(testCases.stream().map(RobotRunnableUnitExecutionInfo::copy).toList());
        config.setTasks(tasks.stream().map(RobotRunnableUnitExecutionInfo::copy).toList());
        config.setDirectories(directories);
        return config;
    }

    public static class RobotRunnableUnitExecutionInfo {

        private String location;
        private String unitName;

        protected RobotRunnableUnitExecutionInfo() {}

        public RobotRunnableUnitExecutionInfo(String fqdn) {
            int unitNameStartIndex = fqdn.lastIndexOf('.');
            if (unitNameStartIndex == -1) {
                location = "";
                unitName = fqdn;
            } else {
                location = fqdn.substring(0, unitNameStartIndex);
                unitName = fqdn.substring(unitNameStartIndex + 1);
            }
        }

        public void readExternal(Element element) throws InvalidDataException {
            location = JDOMExternalizerUtil.readField(element, "location");
            unitName = JDOMExternalizerUtil.readField(element, "unitName");
        }

        public void writeExternal(Element element) throws WriteExternalException {
            JDOMExternalizerUtil.writeField(element, "location", location);
            JDOMExternalizerUtil.writeField(element, "unitName", unitName);
        }

        public RobotRunnableUnitExecutionInfo copy() {
            RobotRunnableUnitExecutionInfo executionInfo = new RobotRunnableUnitExecutionInfo();
            executionInfo.location = location;
            executionInfo.unitName = unitName;
            return executionInfo;
        }

        public String getLocation() {
            return location;
        }

        public String getUnitName() {
            return unitName;
        }

        public String getFqdn() {
            return location + "." + unitName;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof RobotRunnableUnitExecutionInfo that)) {
                return false;
            }
            return Objects.equals(location, that.location) && Objects.equals(unitName, that.unitName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, unitName);
        }

        @Override
        public String toString() {
            return "location='" + location + '\'' + ", unitName='" + unitName + '\'';
        }
    }
}
