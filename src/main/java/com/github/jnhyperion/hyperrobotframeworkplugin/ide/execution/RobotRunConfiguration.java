package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.ui.editor.RobotConfigurationFragmentedEditor;
import com.intellij.execution.EnvFilesOptions;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.AbstractRunConfiguration;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.PythonConfigurationType;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class RobotRunConfiguration extends AbstractRunConfiguration implements EnvFilesOptions {

    private final PythonRunConfiguration pythonRunConfiguration = (PythonRunConfiguration) PythonConfigurationType.getInstance()
                                                                                                                  .getFactory()
                                                                                                                  .createTemplateConfiguration(getProject());

    public RobotRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
        super(project, configurationFactory);

        pythonRunConfiguration.setUseModuleSdk(true);
        pythonRunConfiguration.setModuleMode(true);
        pythonRunConfiguration.setScriptName("robotcode");
    }

    @Override
    public Collection<Module> getValidModules() {
        return AbstractPythonRunConfiguration.getValidModules(getProject());
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
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);

        pythonRunConfiguration.writeExternal(element);
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
}
