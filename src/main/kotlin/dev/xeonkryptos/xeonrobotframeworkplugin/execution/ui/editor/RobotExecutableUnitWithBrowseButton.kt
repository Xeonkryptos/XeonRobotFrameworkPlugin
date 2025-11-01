package dev.xeonkryptos.xeonrobotframeworkplugin.execution.ui.editor

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.ide.util.AbstractTreeClassChooserDialog
import com.intellij.ide.util.TreeChooser
import com.intellij.ide.util.gotoByName.GotoSymbolModel2
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.TextAccessor
import com.intellij.ui.components.installFileCompletionAndBrowseDialog
import com.intellij.util.TextFieldCompletionProvider
import com.intellij.util.textCompletion.TextFieldWithCompletion
import com.intellij.util.ui.JBUI
import com.jetbrains.python.extensions.ContextAnchor
import com.jetbrains.python.extensions.getQName
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import dev.xeonkryptos.xeonrobotframeworkplugin.gotocontributor.RobotGotoClassContributor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TaskNameIndex
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TestCaseNameIndex
import java.awt.event.ActionListener
import javax.swing.tree.DefaultMutableTreeNode

class RobotTextFieldWithDirectoryBrowseButton(contextAnchor: ContextAnchor) : TextFieldWithBrowseButton() {
    init {
        val textComponentAccessor = TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        installFileCompletionAndBrowseDialog(
            contextAnchor.project,
            this,
            textField,
            FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(RobotFeatureFileType.getInstance()),
            textComponentAccessor
        )
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
), TextAccessor {

    private val validator = ComponentValidator(this).withValidator {
        val txt = text.trim()
        if (txt.isEmpty()) return@withValidator null

        val mode = unitModeProvider()
        return@withValidator if (!isValidQualifiedName(txt, mode)) {
            childComponent.putClientProperty("JComponent.outline", "error")
            ValidationInfo(RobotBundle.message("robot.symbol.validation.invalid"), childComponent)
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
                setText(element.qualifiedName)
            }

            is RobotFile -> {
                setText(element.getQName()?.toString())
            }
        }
    }
    private val documentListener = object : DocumentListener {
        override fun documentChanged(event: DocumentEvent) {
            validator.revalidate()
        }
    }

    init {
        addActionListener(actionListener)
        childComponent.document.addDocumentListener(documentListener)
        childComponent.border = JBUI.Borders.empty(2)
    }

    override fun getText(): String = childComponent.text

    override fun setText(text: String?) {
        childComponent.setText(text)
    }

    private fun isValidQualifiedName(fqName: String, executionMode: RobotTestExecutionMode): Boolean {
        return when (executionMode) {
            RobotTestExecutionMode.TEST_CASES -> TestCaseNameIndex.find(extractLeafName(fqName), contextAnchor.project, contextAnchor.scope)
                ?.any { it is RobotQualifiedNameOwner && it.qualifiedName == fqName } ?: false

            RobotTestExecutionMode.TASKS -> TaskNameIndex.find(extractLeafName(fqName), contextAnchor.project, contextAnchor.scope)
                ?.any { it is RobotQualifiedNameOwner && it.qualifiedName == fqName } ?: false

            else -> false
        }
    }

    private fun extractLeafName(fqName: String): String = fqName.substringAfterLast('.')

    override fun dispose() {
        super.dispose()
        removeActionListener(actionListener)
        childComponent.document.removeDocumentListener(documentListener)
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
