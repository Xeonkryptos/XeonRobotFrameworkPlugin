// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.ui.editor

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.config.RobotRunConfiguration
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.openapi.vfs.VirtualFileManager
import com.jetbrains.python.run.EnvFileComponent.Companion.createEnvFilesFragment
import com.jetbrains.python.run.configuration.PyInterpreterModeNotifier
import javax.swing.JPanel
import kotlin.io.path.Path

class RobotPluginCommonFragmentsBuilder : RobotCommonFragmentsBuilder() {
    override fun createEnvironmentFragments(
        fragments: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>>,
        config: RobotRunConfiguration
    ) {
        val sdkFragment: SettingsEditorFragment<RobotRunConfiguration, JPanel> = RobotPluginSdkFragment()
        fragments.add(sdkFragment)

        fragments.add(createWorkingDirectoryFragment(config.project))
        fragments.add(createEnvParameters())
        fragments.add(createEnvFilesFragment { VirtualFileManager.getInstance().findFileByNioPath(Path(config.pythonRunConfiguration.workingDirectorySafe)) })
        fragments.add(RobotPathMappingsEditorFragment(sdkFragment as PyInterpreterModeNotifier))
    }
}
