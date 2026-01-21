package dev.xeonkryptos.xeonrobotframeworkplugin.execution.ui.editor

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.ide.util.AbstractTreeClassChooserDialog
import com.intellij.ide.util.TreeChooser
import com.intellij.ide.util.gotoByName.GotoSymbolModel2
import com.intellij.openapi.application.EDT
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.getUserData
import com.intellij.openapi.ui.putUserData
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.TextAccessor
import com.intellij.ui.components.installFileCompletionAndBrowseDialog
import com.intellij.util.TextFieldCompletionProvider
import com.intellij.util.textCompletion.TextFieldWithCompletion
import com.intellij.util.ui.JBUI
import com.jetbrains.python.extensions.ContextAnchor
import com.sun.java.accessibility.util.SwingEventMonitor.removeDocumentListener
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.gotocontributor.RobotGotoClassContributor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TaskNameIndex
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TestCaseNameIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.event.ActionListener
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.io.path.Path
import javax.swing.event.DocumentEvent as SwingDocumentEvent
import javax.swing.event.DocumentListener as SwingDocumentListener

private val ROBOT_UNIT_LOCATION_KEY = Key.create<String>("ROBOT_UNIT_LOCATION_KEY")
private val ROBOT_UNIT_NAME_KEY = Key.create<String?>("ROBOT_UNIT_NAME_KEY")

interface RobotUnitLocationProvider {

    val qualifiedLocation: String
    val unitName: String?

    fun updateUnitLocation(newLocation: String, unitName: String? = null)
}

class RobotTextFieldWithDirectoryBrowseButton(private val contextAnchor: ContextAnchor) : TextFieldWithBrowseButton(), RobotUnitLocationProvider {

    override val qualifiedLocation: String
        get() = getUserData(ROBOT_UNIT_LOCATION_KEY) ?: ""

    override val unitName: String?
        get() = null

    private val validator = ComponentValidator(this).installOn(childComponent)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val textChanges = MutableStateFlow("")

    private val documentListener: SwingDocumentListener = object : DocumentAdapter() {
        override fun textChanged(e: SwingDocumentEvent) {
            textChanges.value = textField.text
            updateUnitLocation(textField.text)
        }
    }

    init {
        val textComponentAccessor = TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        installFileCompletionAndBrowseDialog(
            contextAnchor.project,
            this,
            textField,
            FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(RobotFeatureFileType.getInstance()),
            textComponentAccessor
        )
        addDocumentListener(documentListener)

        scope.launch {
            @Suppress("OPT_IN_USAGE") textChanges.asStateFlow().map { it.trim() }.distinctUntilChanged().debounce(200L).collectLatest { raw ->
                if (raw.isEmpty()) {
                    withContext(Dispatchers.EDT) { applyValidationResult(null) }
                    return@collectLatest
                }

                val pathParts = raw.split("\\", "/").toTypedArray()

                val valid = withContext(Dispatchers.IO) {
                    contextAnchor.getRoots().any { VfsUtil.findFile(Path(raw), false) != null || VfsUtil.findRelativeFile(it, *pathParts) != null }
                }

                withContext(Dispatchers.EDT) {
                    if (valid) {
                        applyValidationResult(null)
                    } else {
                        applyValidationResult(
                            ValidationInfo(
                                RobotBundle.message("robot.run.configuration.fragment.unit.directory.validation.invalid"), childComponent
                            )
                        )
                    }
                }
            }
        }
    }

    override fun updateUnitLocation(newLocation: String, unitName: String?) {
        putUserData(ROBOT_UNIT_LOCATION_KEY, newLocation)
        putUserData(ROBOT_UNIT_NAME_KEY, null)
        text = newLocation
    }

    private fun applyValidationResult(info: ValidationInfo?) {
        if (info == null) {
            childComponent.putClientProperty("JComponent.outline", null)
            validator.updateInfo(null)
        } else {
            childComponent.putClientProperty("JComponent.outline", "error")
            validator.updateInfo(info)
        }
    }

    override fun dispose() {
        super.dispose()
        removeDocumentListener(documentListener)
        scope.cancel()
    }
}

class RobotExecutableUnitWithBrowseButton(
    private val contextAnchor: ContextAnchor, unitModeProvider: () -> RobotTestExecutionMode
) : ComponentWithBrowseButton<TextFieldWithCompletion>(
    TextFieldWithCompletion(
        contextAnchor.project, RobotExecutionUnitCompletionProvider(
            contextAnchor, unitModeProvider
        ), "", true, true, true
    ), null
), TextAccessor, RobotUnitLocationProvider {

    companion object {
        private val escapedDotRegex = Regex("""^.*(?<!\\)\.""", RegexOption.DOT_MATCHES_ALL)
        private val escapedDotReplaceRegex = """\\\.""".toRegex()

        internal fun extractLeafName(fqName: String): String = fqName.replace(escapedDotRegex, "").replace(escapedDotReplaceRegex, ".")

        internal fun extractLocation(qualifiedName: String, elementName: String?): String =
            qualifiedName.take(qualifiedName.length - (elementName?.replace(".", "\\.")?.length?.plus(1) ?: 0))
    }

    override val qualifiedLocation: String
        get() = getUserData(ROBOT_UNIT_LOCATION_KEY) ?: ""

    override val unitName: String?
        get() = getUserData(ROBOT_UNIT_NAME_KEY)

    private val validator = ComponentValidator(this).withValidator {
        val txt = text.trim()
        if (txt.isEmpty()) return@withValidator null

        val mode = unitModeProvider()
        return@withValidator if (!isValidQualifiedName(qualifiedLocation, unitName, mode)) {
            childComponent.putClientProperty("JComponent.outline", "error")
            ValidationInfo(RobotBundle.message("robot.run.configuration.fragment.unit.symbol.validation.invalid"), childComponent)
        } else {
            childComponent.putClientProperty("JComponent.outline", null)
            null
        }
    }.installOn(childComponent)

    private val actionListener = ActionListener {
        val dialog = RobotSymbolChooserDialog(contextAnchor.project, contextAnchor.scope) { element ->
            when (unitModeProvider()) {
                RobotTestExecutionMode.TEST_CASES -> element is RobotTestCaseStatement
                RobotTestExecutionMode.TASKS -> element is RobotTaskStatement
                else -> false
            }
        }
        dialog.showDialog()
        when (val element = dialog.selected) {
            is RobotQualifiedNameOwner -> {
                val elementName = element.name
                val qualifiedName = element.qualifiedName
                val qualifiedLocation = extractLocation(qualifiedName, elementName)
                updateUnitLocation(qualifiedLocation, elementName)
            }
        }
    }

    private val documentListener = object : DocumentListener {
        override fun documentChanged(event: DocumentEvent) {
            val qualifiedName = text.trim()
            val elementName = extractLeafName(qualifiedName)
            val qualifiedLocation = extractLocation(qualifiedName, elementName)
            when (unitModeProvider()) {
                RobotTestExecutionMode.TEST_CASES, RobotTestExecutionMode.TASKS -> {
                    putUserData(ROBOT_UNIT_LOCATION_KEY, qualifiedLocation)
                    putUserData(ROBOT_UNIT_NAME_KEY, elementName)
                }

                RobotTestExecutionMode.DIRECTORIES -> {
                    putUserData(ROBOT_UNIT_LOCATION_KEY, elementName)
                    putUserData(ROBOT_UNIT_NAME_KEY, null)
                }
            }

            validator.revalidate()
        }
    }

    init {
        addActionListener(actionListener)
        childComponent.document.addDocumentListener(documentListener, this)
        childComponent.border = JBUI.Borders.empty(2)
    }

    override fun updateUnitLocation(newLocation: String, unitName: String?) {
        putUserData(ROBOT_UNIT_LOCATION_KEY, newLocation)
        putUserData(ROBOT_UNIT_NAME_KEY, unitName)
        text = "${newLocation}.${unitName?.replace(".", "\\.") ?: ""}"
    }

    override fun getText(): String = childComponent.text

    override fun setText(text: String?) {
        childComponent.setText(text)
    }

    private fun isValidQualifiedName(location: String, unitName: String?, executionMode: RobotTestExecutionMode): Boolean {
        val fqdn = "$location.$unitName"
        return when (executionMode) {
            RobotTestExecutionMode.TEST_CASES -> TestCaseNameIndex.find(unitName, contextAnchor.project, contextAnchor.scope)
                ?.any { it is RobotQualifiedNameOwner && it.qualifiedName == fqdn } ?: false

            RobotTestExecutionMode.TASKS -> TaskNameIndex.find(unitName, contextAnchor.project, contextAnchor.scope)
                ?.any { it is RobotQualifiedNameOwner && it.qualifiedName == fqdn } ?: false

            else -> false
        }
    }

    override fun dispose() {
        super.dispose()
        removeActionListener(actionListener)
    }
}

private class RobotSymbolChooserDialog(
    project: Project, scope: GlobalSearchScope, filter: ((PsiElement) -> Boolean)?
) : AbstractTreeClassChooserDialog<PsiNamedElement>(
    RobotBundle.message("robot.symbol.chooser.dialog.title"),
    project,
    scope,
    PsiNamedElement::class.java,
    TreeChooser.Filter { filter?.invoke(it) ?: true },
    null
) {

    override fun getSelectedFromTreeUserObject(node: DefaultMutableTreeNode?): Nothing? = null

    override fun getClassesByName(
        name: String, checkBoxState: Boolean, pattern: String?, searchScope: GlobalSearchScope
    ): MutableList<PsiNamedElement> = findElements(name, searchScope).filter(filter::isAccepted).toMutableList()

    fun findElements(name: String, searchScope: GlobalSearchScope): Collection<PsiNamedElement> =
        TestCaseNameIndex.find(name, project, searchScope) + TaskNameIndex.find(name, project, searchScope)

    override fun createChooseByNameModel(): GotoSymbolModel2 {
        return GotoSymbolModel2(project, listOf(RobotGotoClassContributor()), disposable)
    }
}

private class RobotExecutionUnitCompletionProvider(private val contextAnchor: ContextAnchor, private val unitModeProvider: () -> RobotTestExecutionMode) :
    TextFieldCompletionProvider(true) {

    override fun addCompletionVariants(
        text: String, offset: Int, prefix: String, result: CompletionResultSet
    ) {
        if (text.isBlank()) return

        val executionMode = unitModeProvider()
        val relevantElements = mutableSetOf<LookupElement>()
        val prefixMatcher = result.prefixMatcher
        when (executionMode) {
            RobotTestExecutionMode.TEST_CASES -> {
                TestCaseNameIndex.processAllKeys(contextAnchor.scope, null) { elementName ->
                    TestCaseNameIndex.find(elementName, contextAnchor.project, contextAnchor.scope)?.forEach { element ->
                        val lookupElement = createLookupElementBuilder(element)
                        if (prefixMatcher.prefixMatches(lookupElement)) {
                            relevantElements.add(lookupElement)
                        }
                    }
                    true
                }
            }

            RobotTestExecutionMode.TASKS -> {
                val prefixMatcher = result.prefixMatcher
                TaskNameIndex.processAllKeys(contextAnchor.scope, null) { elementName ->
                    TaskNameIndex.find(elementName, contextAnchor.project, contextAnchor.scope)?.forEach { element ->
                        val lookupElement = createLookupElementBuilder(element)
                        if (prefixMatcher.prefixMatches(lookupElement)) {
                            relevantElements.add(lookupElement)
                        }
                    }
                    true
                }
            }

            else -> {
                // No completion for directories
            }
        }
        result.addAllElements(relevantElements)
    }

    private fun createLookupElementBuilder(element: RobotQualifiedNameOwner): LookupElement {
        val qualifiedName = element.qualifiedName
        return LookupElementBuilder.createWithIcon(element)
            .withLookupStrings(listOf(qualifiedName))
            .withCaseSensitivity(false)
            .withInsertHandler(FqdnInsertHandler())
            .withTailText(qualifiedName.substringBeforeLast("."))
    }

    private class FqdnInsertHandler : InsertHandler<LookupElement> {

        override fun handleInsert(context: InsertionContext, item: LookupElement) {
            val element = item.psiElement as RobotQualifiedNameOwner
            context.document.replaceString(0, context.document.textLength, element.qualifiedName)
        }
    }
}
