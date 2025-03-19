// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.ui.editor

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.RobotRunConfiguration
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.ui.CommonParameterFragments
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.ide.macro.MacrosDialog
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.Predicates
import com.intellij.ui.components.fields.ExtendableTextField
import java.awt.BorderLayout
import javax.swing.JComponent

abstract class RobotCommonFragmentsBuilder {
    abstract fun createEnvironmentFragments(
        fragments: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>>,
        config: RobotRunConfiguration
    )

    fun createWorkingDirectoryFragment(
        project: Project
    ): SettingsEditorFragment<RobotRunConfiguration, LabeledComponent<TextFieldWithBrowseButton>> {
        val textField = ExtendableTextField(10)
        val workingDirectoryField = TextFieldWithBrowseButton(textField)
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().withTitle(ExecutionBundle.message("select.working.directory.message"))
        workingDirectoryField.addBrowseFolderListener(project, descriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT)
        val field = LabeledComponent.create(
            workingDirectoryField,
            ExecutionBundle.message("run.configuration.working.directory.label"),
            BorderLayout.WEST
        )
        val workingDirectorySettings = SettingsEditorFragment(
            "workingDirectory",
            ExecutionBundle.message("run.configuration.working.directory.name"), null, field,
            { config: RobotRunConfiguration, component: LabeledComponent<TextFieldWithBrowseButton> -> component.component.text = config.workingDirectory },
            { config: RobotRunConfiguration, component: LabeledComponent<TextFieldWithBrowseButton> -> config.workingDirectory = component.component.text },
            Predicates.alwaysTrue()
        )
        MacrosDialog.addMacroSupport(workingDirectoryField.textField as ExtendableTextField, MacrosDialog.Filters.ALL) { false }
        workingDirectorySettings.isRemovable = false
        return workingDirectorySettings
    }

    fun createEnvParameters(): SettingsEditorFragment<RobotRunConfiguration, *> {
        val env = EnvironmentVariablesComponent()
        env.labelLocation = BorderLayout.WEST

        CommonParameterFragments.setMonospaced(env.component.textField)
        val fragment = SettingsEditorFragment<RobotRunConfiguration, JComponent>(
            "environmentVariables",
            ExecutionBundle.message("environment.variables.fragment.name"),
            ExecutionBundle.message("group.operating.system"), env,
            { config: RobotRunConfiguration, _: JComponent? ->
                env.envs = config.envs
                env.isPassParentEnvs = config.isPassParentEnvs
            },
            { config: RobotRunConfiguration, _: JComponent? ->
                if (!env.isVisible) {
                    config.envs = emptyMap()
                    config.isPassParentEnvs = true
                } else {
                    config.envs = env.envs
                    config.isPassParentEnvs = env.isPassParentEnvs
                }
            },
            { true })

        // See com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
        // Placeholder text in EnvironmentVariablesTextFieldWithBrowseButton is disabled in PyCharm for two key reasons:
        // 1. Consistency: Aligns with other fields in SettingsEditorFragmentType.EDITOR group which don't use a placeholder text.
        // 2. Redundancy: Fields in this group are already labeled, making additional placeholder text unnecessary.
        // This decision supports a cleaner, more uniform UI in PyCharm.
        env.myEnvVars.textField.emptyText.setText("")

        fragment.isCanBeHidden = true
        fragment.setHint(ExecutionBundle.message("environment.variables.fragment.hint"))
        fragment.actionHint = ExecutionBundle.message("set.custom.environment.variables.for.the.process")
        return fragment
    }
}
