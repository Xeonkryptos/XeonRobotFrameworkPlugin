package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.ui.editor

import ai.grazie.utils.applyIf
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.Module
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.FixedSizeButton
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.util.Disposer
import com.intellij.ui.TextAccessor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.python.extensions.ContextAnchor
import com.jetbrains.python.extensions.ModuleBasedContextAnchor
import com.jetbrains.python.extensions.ProjectSdkContextAnchor
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config.RobotRunConfiguration
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionListener
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ContainerEvent
import java.awt.event.ContainerListener
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants
import javax.swing.UIManager

class RobotExecutableUnitFactory(configuration: RobotRunConfiguration) : Disposable {

    companion object {
        private const val MAXIMUM_VISIBLE_BROWSE_BUTTON_COUNT = 5
    }

    val dialogPanel: DialogPanel

    private var mode: RobotTestExecutionMode = RobotTestExecutionMode.TEST_CASES
    private val browseButtonContainer: RobotBrowseButtonContainer
    private val scrollPane: JBScrollPane

    private val browseButtonContainerContainerListener = object : ContainerListener {
        override fun componentAdded(e: ContainerEvent?) {
            updateScrollPaneHeight()
            dialogPanel.revalidate()
        }

        override fun componentRemoved(e: ContainerEvent?) {
            updateScrollPaneHeight()
            dialogPanel.revalidate()
        }
    }
    private val browseButtonContainerComponentListener = object : ComponentAdapter() {
        override fun componentResized(e: ComponentEvent?) {
            updateScrollPaneHeight()
        }
    }

    init {
        val project = configuration.pythonRunConfiguration.project
        val module: Module? = configuration.pythonRunConfiguration.module
        val sdk = configuration.pythonRunConfiguration.sdk

        val contextAnchor = if (module == null) ProjectSdkContextAnchor(project, sdk) else ModuleBasedContextAnchor(module)
        browseButtonContainer = RobotBrowseButtonContainer(contextAnchor, mode).apply {
            addContainerListener(browseButtonContainerContainerListener)
            addComponentListener(browseButtonContainerComponentListener)
        }

        scrollPane = JBScrollPane(browseButtonContainer).apply {
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            border = null
        }

        dialogPanel = panel {
            buttonsGroup {
                row(RobotBundle.message("robot.run.configuration.fragments.choose.mode.title")) {
                    radioButton(
                        RobotBundle.message("robot.run.configuration.fragments.choose.mode.test-cases"), RobotTestExecutionMode.TEST_CASES
                    ).align(AlignX.FILL)
                    radioButton(RobotBundle.message("robot.run.configuration.fragments.choose.mode.tasks"), RobotTestExecutionMode.TASKS).align(AlignX.FILL)
                    radioButton(
                        RobotBundle.message("robot.run.configuration.fragments.choose.mode.directories"), RobotTestExecutionMode.DIRECTORIES
                    ).align(AlignX.FILL)
                }
            }.bind(MutableProperty({ mode }, { onModeChanged(it) }), RobotTestExecutionMode::class.java)
            row {
                button("") {
                    browseButtonContainer.addNewBrowseButton()
                    updateScrollPaneHeight()
                    dialogPanel.revalidate()
                }.align(AlignX.FILL).applyToComponent { icon = AllIcons.General.Add }
            }
            row {
                cell(scrollPane).align(AlignX.FILL).onApply {
                    configuration.testCases = browseButtonContainer.getTestCases().map { RobotRunConfiguration.RobotRunnableUnitExecutionInfo(it) }
                    configuration.tasks = browseButtonContainer.getTasks().map { RobotRunConfiguration.RobotRunnableUnitExecutionInfo(it) }
                    configuration.directories = browseButtonContainer.getDirectories().map { it }
                }.onReset {
                    mode = browseButtonContainer.reset(configuration.testCases, configuration.tasks, configuration.directories)
                    updateScrollPaneHeight()
                    dialogPanel.revalidate()
                }
            }
        }.withPreferredWidth(Integer.MAX_VALUE)
        Disposer.register(this, browseButtonContainer)
    }

    private fun onModeChanged(newMode: RobotTestExecutionMode) {
        mode = newMode
        browseButtonContainer.onModeChanged(mode)
        updateScrollPaneHeight()
        dialogPanel.revalidate()
    }

    private fun updateScrollPaneHeight() {
        val first = browseButtonContainer.components.firstOrNull() ?: return
        val rowHeight = first.preferredSize.height
        val visibleCount = minOf(MAXIMUM_VISIBLE_BROWSE_BUTTON_COUNT, browseButtonContainer.componentCount.coerceAtLeast(1))
        val newHeight = rowHeight * visibleCount
        scrollPane.preferredSize = Dimension(scrollPane.preferredSize.width, newHeight)
        scrollPane.maximumSize = Dimension(Int.MAX_VALUE, newHeight)
        scrollPane.revalidate()
    }

    override fun dispose() {
        browseButtonContainer.removeContainerListener(browseButtonContainerContainerListener)
        browseButtonContainer.removeComponentListener(browseButtonContainerComponentListener)
    }
}

private class RobotBrowseButtonContainer(private val contextAnchor: ContextAnchor, initialExecutionMode: RobotTestExecutionMode) : JPanel(),
                                                                                                                                   RobotExecutionModeChangeListener,
                                                                                                                                   Disposable {

    private val containerListener = object : ContainerListener {
        override fun componentAdded(e: ContainerEvent?) {
            updateDeleteButtonsVisibility()
        }

        override fun componentRemoved(e: ContainerEvent?) {
            updateDeleteButtonsVisibility()
        }

        private fun updateDeleteButtonsVisibility() {
            if (components.size == 1) {
                val component = components[0]
                if (component is DeletableBrowseButtonPanel) {
                    component.updateDeleteButtonVisibility(false)
                }
            } else if (components.size > 1) {
                for (comp in components) {
                    if (comp is DeletableBrowseButtonPanel) {
                        comp.updateDeleteButtonVisibility(true)
                    }
                }
            }
        }
    }

    private var executionMode = initialExecutionMode

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        addNewBrowseButton()
        addContainerListener(containerListener)
    }

    fun addNewBrowseButton(initialText: String? = null) {
        val deletableBrowseButtonPanel = DeletableBrowseButtonPanel(contextAnchor, executionMode, initialText)
        Disposer.register(this, deletableBrowseButtonPanel)
        add(deletableBrowseButtonPanel)
    }

    fun getTestCases(): List<String> = getResultForExecutionMode(RobotTestExecutionMode.TEST_CASES)

    fun getTasks(): List<String> = getResultForExecutionMode(RobotTestExecutionMode.TASKS)

    fun getDirectories(): List<String> = getResultForExecutionMode(RobotTestExecutionMode.DIRECTORIES)

    private fun getResultForExecutionMode(expectedMode: RobotTestExecutionMode): List<String> {
        if (executionMode == expectedMode) {
            return components.asSequence().filterIsInstance(DeletableBrowseButtonPanel::class.java).map { it.getText() }.filter { it.isNotBlank() }.toList()
        }
        return emptyList()
    }

    fun reset(
        testCases: List<RobotRunConfiguration.RobotRunnableUnitExecutionInfo>,
        tasks: List<RobotRunConfiguration.RobotRunnableUnitExecutionInfo>,
        directories: List<String>
    ): RobotTestExecutionMode {
        removeAll()

        val newMode = if (testCases.isNotEmpty()) {
            RobotTestExecutionMode.TEST_CASES
        } else if (tasks.isNotEmpty()) {
            RobotTestExecutionMode.TASKS
        } else {
            RobotTestExecutionMode.DIRECTORIES
        }
        onModeChanged(newMode)

        testCases.forEach { addNewBrowseButton("${it.fqdn}") }
        tasks.forEach { addNewBrowseButton("${it.fqdn}") }
        directories.forEach { addNewBrowseButton(it) }
        return newMode
    }

    override fun onModeChanged(newMode: RobotTestExecutionMode) {
        executionMode = newMode
        components.filterIsInstance<RobotExecutionModeChangeListener>().forEach { it.onModeChanged(newMode) }
    }

    override fun dispose() {
        removeContainerListener(containerListener)
    }
}

private class DeletableBrowseButtonPanel(contextAnchor: ContextAnchor, initialExecutionMode: RobotTestExecutionMode, initialText: String? = null) : JPanel(),
                                                                                                                                                    RobotExecutionModeChangeListener,
                                                                                                                                                    Disposable {

    private var executionMode = initialExecutionMode

    private val symbolLabel: LabeledComponent<RobotExecutableUnitWithBrowseButton>
    private val directoryLabel: LabeledComponent<RobotTextFieldWithDirectoryBrowseButton>

    private val newSymbolBrowseButton =
        RobotExecutableUnitWithBrowseButton(contextAnchor) { executionMode }.applyIf(executionMode.unitExecutionMode) { text = initialText ?: "" }
    private val newDirectoryBrowseButton =
        RobotTextFieldWithDirectoryBrowseButton(contextAnchor).applyIf(!executionMode.unitExecutionMode) { text = initialText ?: "" }

    private val symbolDeleteButton: FixedSizeButton
    private val directoryDeleteButton: FixedSizeButton

    private val deleteButtonActionListener: ActionListener = ActionListener { parent.remove(this@DeletableBrowseButtonPanel) }

    private var visibleDeleteButton: Boolean = false

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        Disposer.register(this, newSymbolBrowseButton)
        Disposer.register(this, newDirectoryBrowseButton)

        symbolLabel = LabeledComponent.create(
            newSymbolBrowseButton,
            RobotBundle.message("robot.run.configuration.fragment.executable.unit.label.${executionMode.name.lowercase()}"),
            BorderLayout.WEST
        ).apply { isVisible = false }
        directoryLabel = LabeledComponent.create(
            newDirectoryBrowseButton,
            RobotBundle.message("robot.run.configuration.fragment.executable.unit.label.${executionMode.name.lowercase()}"),
            BorderLayout.WEST
        ).apply { isVisible = false }

        symbolDeleteButton = FixedSizeButton(symbolLabel).apply {
            isVisible = false
            border = UIManager.getBorder("Button.border")
            icon = AllIcons.General.Remove
        }
        symbolDeleteButton.addActionListener(deleteButtonActionListener)
        directoryDeleteButton = FixedSizeButton(directoryLabel).apply {
            isVisible = false
            border = UIManager.getBorder("Button.border")
            icon = AllIcons.General.Remove
        }
        directoryDeleteButton.addActionListener(deleteButtonActionListener)

        add(symbolLabel)
        add(symbolDeleteButton)
        add(directoryLabel)
        add(directoryDeleteButton)
        updateVisibilityState()
    }

    fun getText(): String {
        val label = if (executionMode.unitExecutionMode) symbolLabel else directoryLabel
        return (label.component as TextAccessor).text
    }

    fun updateDeleteButtonVisibility(isVisible: Boolean) {
        visibleDeleteButton = isVisible
        updateVisibilityState()
    }

    override fun onModeChanged(newMode: RobotTestExecutionMode) {
        if (newMode.unitExecutionMode) {
            symbolLabel.text = RobotBundle.message("robot.run.configuration.fragment.executable.unit.label.${newMode.name.lowercase()}")
        } else {
            directoryLabel.text = RobotBundle.message("robot.run.configuration.fragment.executable.unit.label.${newMode.name.lowercase()}")
        }
        executionMode = newMode
        updateVisibilityState()
        newSymbolBrowseButton.text = ""
        newDirectoryBrowseButton.text = ""
    }

    private fun updateVisibilityState() {
        symbolLabel.isVisible = executionMode.unitExecutionMode
        symbolDeleteButton.isVisible = executionMode.unitExecutionMode && visibleDeleteButton
        directoryLabel.isVisible = !executionMode.unitExecutionMode
        directoryDeleteButton.isVisible = !executionMode.unitExecutionMode && visibleDeleteButton
    }

    override fun dispose() {
        newSymbolBrowseButton.removeActionListener(deleteButtonActionListener)
        newDirectoryBrowseButton.removeActionListener(deleteButtonActionListener)
    }
}

private interface RobotExecutionModeChangeListener {

    fun onModeChanged(newMode: RobotTestExecutionMode)
}
