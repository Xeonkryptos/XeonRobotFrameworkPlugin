package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonRunConfiguration;

public class RobotRunConfiguration extends PythonRunConfiguration {

   protected RobotRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
      super(project, configurationFactory);

      this.setEmulateTerminal(true);
   }
}
