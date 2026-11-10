package dev.xeonkryptos.xeonrobotframeworkplugin.execution.ui.editor

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.SimpleListCellRenderer
import com.jetbrains.python.run.AbstractPythonRunConfigurationParams
import com.jetbrains.python.sdk.PythonSdkUtil

/**
 * Minimal replacement of PyCharm's `PySdkComboBox` (based on 2025.3), which changes its constructor and behaviour between platform versions.
 * Lists all known Python SDKs and reads/writes the selection from/to the run configuration.
 */
class RobotSdkComboBox : ComboBox<Sdk>() {
  init {
    renderer = SimpleListCellRenderer.create { label, sdk, _ ->
      if (sdk != null) {
        label.text = sdk.name
        label.icon = (sdk.sdkType as? SdkType)?.icon
      }
    }
  }

  fun reset(config: AbstractPythonRunConfigurationParams) {
    removeAllItems()
    PythonSdkUtil.getAllSdks().forEach(::addItem)
    selectedItem = config.sdk
  }

  fun apply(config: AbstractPythonRunConfigurationParams) {
    config.sdk = selectedItem as? Sdk
  }

  fun getSelectedSdk(): Sdk? = selectedItem as? Sdk
}
