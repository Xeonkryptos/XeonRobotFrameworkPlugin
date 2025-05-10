// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui.editor

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration
import com.intellij.execution.ui.CommandLinePanel
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.execution.util.PathMappingsComponent
import com.jetbrains.python.run.configuration.AbstractPythonConfigurationFragmentedEditor.Companion.MIN_FRAGMENT_WIDTH
import com.jetbrains.python.run.configuration.PyInterpreterModeNotifier
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

class RobotPathMappingsEditorFragment(notifier: PyInterpreterModeNotifier) :
  SettingsEditorFragment<RobotRunConfiguration, JPanel>("py.path.mappings", null, null, JPanel(GridBagLayout()), null, null, { true }) {
  private val pathMappingsComponent = PathMappingsComponent()

  init {
    pathMappingsComponent.labelLocation = BorderLayout.WEST
    CommandLinePanel.setMinimumWidth(component(), MIN_FRAGMENT_WIDTH)
    val constrains = GridBagConstraints()
    constrains.fill = GridBagConstraints.HORIZONTAL
    constrains.weightx = 1.0
    constrains.gridx = 0

    component().add(pathMappingsComponent, constrains)
    pathMappingsComponent.isVisible = notifier.isRemoteSelected()
    notifier.addInterpreterModeListener { isRemote ->
      pathMappingsComponent.isVisible = isRemote
    }
  }

  override fun getAllComponents(): Array<JComponent> {
    return arrayOf(pathMappingsComponent)
  }

  override fun resetEditorFrom(config: RobotRunConfiguration) {
    pathMappingsComponent.setMappingSettings(config.pythonRunConfiguration.mappingSettings)
  }

  override fun applyEditorTo(s: RobotRunConfiguration) {
    s.pythonRunConfiguration.mappingSettings = pathMappingsComponent.mappingSettings
  }

  override fun isRemovable(): Boolean = false

  override fun isSelected(): Boolean = true
}

