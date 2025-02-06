package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ModuleBasedConfigurationOptions;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonRunConfiguration;
import org.jetbrains.annotations.NotNull;

public class RobotRunConfiguration extends PythonRunConfiguration {

   public RobotRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
      super(project, configurationFactory);

      this.setEmulateTerminal(true);
   }

   @NotNull
   @Override
   public ModuleBasedConfigurationOptions getOptions() {
      return super.getOptions();
   }
}
