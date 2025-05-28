package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config;

import com.intellij.configurationStore.ComponentSerializationUtil;
import com.intellij.execution.configurations.ModuleBasedConfigurationOptions;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.python.run.PythonConfigurationType;
import com.jetbrains.python.run.PythonRunConfiguration;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Extends the {@link PythonRunConfiguration} to provide access to {@link #getOptions()} to allow dirty tracking in run configuration dialog
 */
public class PythonRunConfigurationExt extends PythonRunConfiguration {

    public PythonRunConfigurationExt(Project project) {
        super(project, PythonConfigurationType.getInstance().getFactory());
    }

    @NotNull
    @Override
    protected ModuleBasedConfigurationOptions getOptions() {
        return super.getOptions();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "MethodDoesntCallSuperMethod" })
    public PythonRunConfigurationExt clone() {
        PythonRunConfigurationExt configuration = new PythonRunConfigurationExt(getProject());
        configuration.setName(getName());

        // AbstractPythonRunConfiguration calls in the constructor and so, there is a chance that newly created configuration will have module, but old haven't
        // and so, on readExternal module will be lost
        RunConfigurationModule configurationModule = configuration.getConfigurationModule();
        String moduleName = StringUtil.nullize(configurationModule.getModuleName());

        boolean isUseReadWriteExternal = true;
        if (this instanceof PersistentStateComponent<?>) {
            @SuppressWarnings("unchecked")
            Class<?> stateClass = ComponentSerializationUtil.getStateClass((Class<? extends PersistentStateComponent>)getClass());
            if (stateClass != Element.class) {
                isUseReadWriteExternal = false;
                configuration.doCopyOptionsFrom(this);
            }
        }

        if (isUseReadWriteExternal) {
            final Element element = new Element(TO_CLONE_ELEMENT_NAME);
            try {
                writeExternal(element);
                configuration.readExternal(element);
                // we don't call super.clone(), but writeExternal doesn't copy transient fields in the options like isAllowRunningInParallel
                // so, we have to call copyFrom to ensure that state is fully cloned
                // MUST BE AFTER readExternal because readExternal set options to a new instance
                configuration.setAllowRunningInParallel(isAllowRunningInParallel());
            }
            catch (InvalidDataException | WriteExternalException e) {
                MyLogger.logger.error(e);
                return null;
            }
        }
        if (moduleName != null && StringUtil.nullize(configurationModule.getModuleName()) == null) {
            configurationModule.setModuleName(moduleName);
        }
        return configuration;
    }
}
