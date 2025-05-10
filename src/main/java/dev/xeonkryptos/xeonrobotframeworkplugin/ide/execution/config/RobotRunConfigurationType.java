package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class RobotRunConfigurationType implements ConfigurationType {

    private final RobotConfigurationFactory configurationFactory = new RobotConfigurationFactory(this);

    public static RobotRunConfigurationType getRobotRunConfigurationType() {
        return ConfigurationTypeUtil.findConfigurationType(RobotRunConfigurationType.class);
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Robot";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Robot run configuration type";
    }

    @Override
    public Icon getIcon() {
        return RobotIcons.FILE;
    }

    @NotNull
    @Override
    public String getId() {
        return "RobotRunConfiguration";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[] { new RobotConfigurationFactory(this) };
    }

    public final RobotConfigurationFactory getConfigurationFactory() {
        return this.configurationFactory;
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
