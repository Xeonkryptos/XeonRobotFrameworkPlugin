package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public class RobotRunConfigurationType implements ConfigurationType {

   private final RobotConfigurationFactory configurationFactory = new RobotConfigurationFactory(this);

   public static RobotRunConfigurationType getRobotRunConfigurationType() {
      return ConfigurationTypeUtil.findConfigurationType(RobotRunConfigurationType.class);
   }

   @NotNull
   public String getDisplayName() {
      return "Robot";
   }

   public String getConfigurationTypeDescription() {
      return "Robot run configuration type";
   }

   public Icon getIcon() {
      return RobotIcons.FILE;
   }

   @NotNull
   public String getId() {
      return "RobotRunConfiguration";
   }

   public ConfigurationFactory[] getConfigurationFactories() {
      return new ConfigurationFactory[]{new RobotConfigurationFactory(this)};
   }

   public final RobotConfigurationFactory getConfigurationFactory() {
      return this.configurationFactory;
   }
}
