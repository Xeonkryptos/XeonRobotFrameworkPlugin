package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui.editor

import com.intellij.diagnostic.logging.LogsGroupFragment
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.ui.BeforeRunComponent
import com.intellij.execution.ui.BeforeRunFragment
import com.intellij.execution.ui.CommandLinePanel
import com.intellij.execution.ui.CommonParameterFragments
import com.intellij.execution.ui.CommonTags
import com.intellij.execution.ui.RunConfigurationFragmentedEditor
import com.intellij.execution.ui.SettingsEditorFragment
import com.intellij.execution.ui.SettingsEditorFragmentType
import com.intellij.ide.macro.MacrosDialog
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.TextComponentEmptyText
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration
import java.awt.BorderLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

class RobotConfigurationFragmentedEditor(private val runConfiguration: RobotRunConfiguration) : RunConfigurationFragmentedEditor<RobotRunConfiguration>(
    runConfiguration
) {

    override fun createRunFragments(): MutableList<SettingsEditorFragment<RobotRunConfiguration, *>> {
        val fragments: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>> = ArrayList()
        val beforeRunComponent = BeforeRunComponent(this)
        fragments.add(BeforeRunFragment.createBeforeRun(beforeRunComponent, null))
        fragments.addAll(BeforeRunFragment.createGroup())
        fragments.add(CommonParameterFragments.createRunHeader())
        fragments.add(CommonTags.parallelRun())

        createEnvironmentFragments(fragments, runConfiguration)
        addInterpreterOptions(fragments)
        addContentSourceRoots(fragments)

        customizeFragments(fragments)
//        fragments.add(PyEditorExtensionFragment())
        fragments.add(LogsGroupFragment())
        return fragments
    }

    private fun createEnvironmentFragments(
        fragments: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>>, runConfiguration: RobotRunConfiguration
    ) {
        RobotPluginCommonFragmentsBuilder().createEnvironmentFragments(fragments, runConfiguration)
    }

    fun customizeFragments(fragments: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>>) {
        val parametersEditor = RawCommandLineEditor()
        CommandLinePanel.setMinimumWidth(parametersEditor, MIN_FRAGMENT_WIDTH)
        val scriptParametersFragment: SettingsEditorFragment<RobotRunConfiguration, RawCommandLineEditor> =
            SettingsEditorFragment<RobotRunConfiguration, RawCommandLineEditor>(
                "py.script.parameters",
                RobotBundle.getMessage("python.run.configuration.fragments.script.parameters"),
                RobotBundle.getMessage("python.run.configuration.fragments.python.group"),
                parametersEditor,
                SettingsEditorFragmentType.COMMAND_LINE,
                { config: RobotRunConfiguration, field: RawCommandLineEditor -> field.text = config.pythonRunConfiguration.scriptParameters },
                { config: RobotRunConfiguration, field: RawCommandLineEditor -> config.pythonRunConfiguration.scriptParameters = field.text.trim() },
                { true })
        MacrosDialog.addMacroSupport(parametersEditor.editorField, MacrosDialog.Filters.ALL) { false }
        parametersEditor.editorField.emptyText.text = RobotBundle.getMessage("python.run.configuration.fragments.script.parameters.hint")
        TextComponentEmptyText.setupPlaceholderVisibility(parametersEditor.editorField)
        scriptParametersFragment.setHint(RobotBundle.getMessage("python.run.configuration.fragments.script.parameters.hint"))
        scriptParametersFragment.actionHint = RobotBundle.getMessage("python.run.configuration.fragments.script.parameters.hint")
        fragments.add(scriptParametersFragment)

        val runWithConsole = SettingsEditorFragment.createTag<RobotRunConfiguration>(
            "py.run.with.python.console",
            RobotBundle.getMessage("python.run.configuration.fragments.run.with.python.console"),
            RobotBundle.getMessage("python.run.configuration.fragments.python.group"),
            { it.pythonRunConfiguration.showCommandLineAfterwards() },
            { config, value -> config.pythonRunConfiguration.setShowCommandLineAfterwards(value) })
        runWithConsole.actionHint = RobotBundle.getMessage("python.run.configuration.fragments.run.with.python.console.hint")
        fragments.add(runWithConsole)

        val emulateTerminal = SettingsEditorFragment.createTag<RobotRunConfiguration>(
            "py.emulate.terminal",
            RobotBundle.getMessage("python.run.configuration.fragments.emulate.terminal"),
            RobotBundle.getMessage("python.run.configuration.fragments.python.group"),
            { it.pythonRunConfiguration.emulateTerminal() },
            { config, value -> config.pythonRunConfiguration.setEmulateTerminal(value) })
        emulateTerminal.actionHint = RobotBundle.getMessage("python.run.configuration.fragments.emulate.terminal.hint")
        fragments.add(emulateTerminal)

        val inputFile = TextFieldWithBrowseButton()
        inputFile.addBrowseFolderListener(
            TextBrowseFolderListener(
                FileChooserDescriptorFactory.createSingleFileDescriptor(), runConfiguration.project
            )
        )
        val labeledComponent = LabeledComponent.create<TextFieldWithBrowseButton>(inputFile, ExecutionBundle.message("redirect.input.from"))
        labeledComponent.labelLocation = BorderLayout.WEST
        val redirectInputFrom: SettingsEditorFragment<RobotRunConfiguration, LabeledComponent<TextFieldWithBrowseButton>> =
            SettingsEditorFragment<RobotRunConfiguration, LabeledComponent<TextFieldWithBrowseButton>>(
                "py.redirect.input",
                ExecutionBundle.message("redirect.input.from.name"),
                ExecutionBundle.message("group.operating.system"),
                labeledComponent,
                SettingsEditorFragmentType.EDITOR,
                { config, component ->
                    component.component.text = config.pythonRunConfiguration.inputFile
                },
                { config, component ->
                    val filePath = component.component.text
                    config.pythonRunConfiguration.isRedirectInput = component.isVisible && StringUtil.isNotEmpty(filePath)
                    config.pythonRunConfiguration.inputFile = filePath
                },
                { config -> config.pythonRunConfiguration.isRedirectInput })
        redirectInputFrom.actionHint = ExecutionBundle.message("read.input.from.the.specified.file")
        addToFragmentsBeforeEditors(fragments, redirectInputFrom)

        val editors = mutableListOf<SettingsEditorFragment<RobotRunConfiguration, *>>()
        editors.add(runWithConsole)
        editors.add(emulateTerminal)
        editors.add(redirectInputFrom)
        addSingleSelectionListeners(editors)
    }

    private fun addInterpreterOptions(fragments: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>>) {
        val interpreterOptionsField = RawCommandLineEditor()
        val interpreterOptionsFragment: SettingsEditorFragment<RobotRunConfiguration, RawCommandLineEditor> =
            SettingsEditorFragment<RobotRunConfiguration, RawCommandLineEditor>(
                "py.interpreter.options",
                RobotBundle.getMessage("python.run.configuration.fragments.interpreter.options"),
                RobotBundle.getMessage("python.run.configuration.fragments.python.group"),
                interpreterOptionsField,
                SettingsEditorFragmentType.COMMAND_LINE,
                { config: RobotRunConfiguration, field: RawCommandLineEditor -> field.text = config.pythonRunConfiguration.interpreterOptions },
                { config: RobotRunConfiguration, field: RawCommandLineEditor -> config.pythonRunConfiguration.interpreterOptions = field.text.trim() },
                { config: RobotRunConfiguration -> !config.pythonRunConfiguration.interpreterOptions.trim().isEmpty() })
        interpreterOptionsField.editorField.emptyText.setText(RobotBundle.getMessage("python.run.configuration.fragments.interpreter.options.placeholder"))
        TextComponentEmptyText.setupPlaceholderVisibility(interpreterOptionsField.editorField)
        interpreterOptionsFragment.setHint(RobotBundle.getMessage("python.run.configuration.fragments.interpreter.options.hint"))
        interpreterOptionsFragment.actionHint = RobotBundle.getMessage("python.run.configuration.fragments.interpreter.options.hint")
        fragments.add(interpreterOptionsFragment)
    }

    private fun addContentSourceRoots(fragments: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>>) {
        val addContentRoots = SettingsEditorFragment.createTag<RobotRunConfiguration>(
            "py.add.content.roots",
            RobotBundle.getMessage("python.run.configuration.fragments.content.roots"),
            RobotBundle.getMessage("python.run.configuration.fragments.python.group"),
            { it.pythonRunConfiguration.shouldAddContentRoots() },
            { config, value -> config.pythonRunConfiguration.setAddContentRoots(value) })

        addContentRoots.actionHint = RobotBundle.getMessage("python.run.configuration.fragments.content.roots.hint")
        fragments.add(addContentRoots)

        val addSourceRoots = SettingsEditorFragment.createTag<RobotRunConfiguration>(
            "py.add.source.roots",
            RobotBundle.getMessage("python.run.configuration.fragments.source.roots"),
            RobotBundle.getMessage("python.run.configuration.fragments.python.group"),
            { it.pythonRunConfiguration.shouldAddSourceRoots() },
            { config, value -> config.pythonRunConfiguration.setAddSourceRoots(value) })

        addSourceRoots.actionHint = RobotBundle.getMessage("python.run.configuration.fragments.source.roots.hint")
        fragments.add(addSourceRoots)
    }

    private fun addSingleSelectionListeners(editors: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>>) {
        for ((i, editor) in editors.withIndex()) {
            editor.component().addComponentListener(object : ComponentAdapter() {
                override fun componentShown(e: ComponentEvent?) {
                    for ((j, otherEditor) in editors.withIndex()) {
                        if (i != j) {
                            otherEditor.isSelected = false
                        }
                    }
                }
            })
        }
    }

    private fun addToFragmentsBeforeEditors(
        fragments: MutableList<SettingsEditorFragment<RobotRunConfiguration, *>>, newFragment: SettingsEditorFragment<RobotRunConfiguration, *>
    ) {
        // Q: not sure whether it makes sense to make it more generic, not only for EDITOR type
        val index = fragments.indexOfFirst { it.isEditor }
        if (index == -1) {
            fragments.add(newFragment)
        } else {
            fragments.add(index, newFragment)
        }
    }

    companion object {
        const val MIN_FRAGMENT_WIDTH = 500
    }
}
