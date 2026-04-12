package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.CodeStyle
import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.startOffset
import com.intellij.ui.tree.TreeTestUtil.node
import com.intellij.util.DocumentUtil
import com.jetbrains.python.ast.findChildByType
import com.jetbrains.python.ast.impl.PyPsiUtilsCore
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElseIfStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElseStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptHeader
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptionHandlingStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFinallyStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopHeader
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGroupHeader
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGroupStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotIfStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineElseIfStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineElseStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineIfStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLoopControlStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotReturnStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTryStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopHeader
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopStructure
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants
import org.toml.lang.psi.ext.elementType


/**
 * PreFormatProcessor for Robot Framework files.
 *
 * This processor collapses existing multi-line statements (those using continuation markers
 * "...") into single-line statements before the formatter runs. This is necessary because
 * the formatter's wrapping logic needs to work with single-line statements to correctly
 * decide where to introduce new line breaks.
 *
 * **How it works:**
 *
 * In the Robot Framework lexer, a continuation sequence consists of three consecutive
 * WHITE_SPACE tokens:
 * 1. `"\n    "` — the newline and leading indentation (MultiLineStart)
 * 2. `"..."`   — the continuation marker itself (MultiLineContinuation)
 * 3. `"    "`  — the separator space after the marker (AFTER_CONTINUATION)
 *
 * This processor finds WHITE_SPACE nodes whose text equals `"..."`, then replaces the
 * entire three-token sequence (preceding newline/indent + `"..."` + trailing space) with
 * a simple super-space separator (`"  "`). This collapses the continuation into a single
 * line while preserving valid Robot Framework syntax (elements are separated by ≥2 spaces).
 *
 * After collapsing, the AST is reparsed and all statements are single-line, allowing the
 * formatter to apply wrapping decisions cleanly. The [RobotPostFormatProcessor] then
 * re-inserts continuation markers where the formatter introduced new line breaks.
 */
class RobotPreFormatProcessor : PreFormatProcessor {

    override fun process(element: ASTNode, range: TextRange): TextRange {
        val psi = element.psi ?: return range
        if (psi.language !== RobotLanguage.INSTANCE) return range
        @Suppress("UnstableApiUsage") PyPsiUtilsCore.assertValid(psi)
        val file = psi.containingFile ?: return range

        val metadata = RobotStatementMetadataCollector().process(psi, range)
        val newStartOffset = metadata.map { it.initialTextRange.startOffset }.filter { it < range.startOffset }.minOrNull() ?: range.startOffset
        val newEndOffset = metadata.map { it.initialTextRange.endOffset }.filter { it > range.endOffset }.maxOrNull() ?: range.endOffset

        var currentRange = TextRange(newStartOffset, newEndOffset)
        val commonSettings = CodeStyle.getLanguageSettings(file, RobotLanguage.INSTANCE)
        if (!commonSettings.KEEP_LINE_BREAKS) {
            val psiDocumentManager = PsiDocumentManager.getInstance(psi.project)
            val document = file.fileDocument
            DocumentUtil.executeInBulk(document) {
                psiDocumentManager.doPostponedOperationsAndUnblockDocument(document)
                currentRange = RobotContinuationWhitespaceAroundRemover(psi.project).process(psi, currentRange)
                currentRange = RobotContinuationMarkerReplacer(psi.project).process(psi, currentRange)
                psiDocumentManager.commitDocument(document)
            }
        }
        return currentRange
    }

    override fun changesWhitespacesOnly(): Boolean = true

    private abstract class RobotContinuationSequenceRemover(project: Project) : RecursiveRobotVisitor() {

        protected val elementModifications = mutableListOf<ElementModificator>()

        protected val superSpaceElement = PsiParserFacade.getInstance(project).createWhiteSpaceFromText(GlobalConstants.SUPER_SPACE)
        protected val smartPointerManager: SmartPointerManager = SmartPointerManager.getInstance(project)

        private lateinit var range: TextRange

        fun process(element: PsiElement, range: TextRange): TextRange {
            this.range = range

            element.accept(this)
            elementModifications.sortDescending()
            val deletedCharactersCount = elementModifications.sumOf { it.process() }
            return range.grown(-deletedCharactersCount)
        }

        override fun visitWhiteSpace(space: PsiWhiteSpace) {
            if (!space.textRange.intersectsStrict(range)) return
            if (space.textMatches(GlobalConstants.CONTINUATION) && !isCommentBeforeContinuationMarker(space)) {
                visitContinuationMarker(space)
            }
        }

        protected abstract fun visitContinuationMarker(continuationMarkerElement: PsiWhiteSpace)

        private fun isCommentBeforeContinuationMarker(startSpace: PsiElement): Boolean {
            var currentElement: PsiElement? = startSpace
            while (currentElement != null) {
                if (currentElement.elementType === RobotTypes.COMMENT) return true
                if (currentElement.elementType !== TokenType.WHITE_SPACE) return false
                currentElement = currentElement.prevSibling
            }
            return false
        }

        protected interface ElementModificator : Comparable<ElementModificator> {
            val startOffset: Int

            fun process(): Int

            override fun compareTo(other: ElementModificator): Int = startOffset.compareTo(other.startOffset)
        }
    }

    private class RobotContinuationWhitespaceAroundRemover(project: Project) : RobotContinuationSequenceRemover(project) {

        override fun visitContinuationMarker(continuationMarkerElement: PsiWhiteSpace) {
            collectRemovableWhiteSpaces(continuationMarkerElement) { it.prevSibling }
            collectRemovableWhiteSpaces(continuationMarkerElement) { it.nextSibling }
        }

        private fun collectRemovableWhiteSpaces(startSpace: PsiElement, treeMover: (currentElement: PsiElement) -> PsiElement?) {
            var currentElement = treeMover(startSpace)
            while (currentElement?.elementType === TokenType.WHITE_SPACE) {
                val deletableElement = currentElement
                val startOffset = deletableElement.startOffset
                val smartPointer = smartPointerManager.createSmartPsiElementPointer<PsiElement>(deletableElement)
                elementModifications.add(object : ElementModificator {
                    override val startOffset: Int = startOffset

                    override fun process(): Int {
                        val result = smartPointer.element?.let {
                            val textLength = it.textLength
                            it.delete()
                            return@let textLength
                        }
                        smartPointerManager.removePointer(smartPointer)
                        return result ?: 0
                    }
                })

                currentElement = treeMover(currentElement)
            }
        }
    }

    private class RobotContinuationMarkerReplacer(project: Project) : RobotContinuationSequenceRemover(project) {
        override fun visitContinuationMarker(continuationMarkerElement: PsiWhiteSpace) {
            val startOffset = continuationMarkerElement.startOffset
            val smartPointer = smartPointerManager.createSmartPsiElementPointer<PsiElement>(continuationMarkerElement)
            elementModifications.add(object : ElementModificator {
                override val startOffset: Int = startOffset

                override fun process(): Int {
                    val result = smartPointer.element?.let {
                        it.replace(superSpaceElement)
                        return@let 1
                    }
                    smartPointerManager.removePointer(smartPointer)
                    return result ?: 0
                }
            })
        }
    }
}

class RobotMetadataCollectorPreFormatProcessor : PreFormatProcessor {

    override fun process(element: ASTNode, range: TextRange): TextRange {
        val psi = element.psi ?: return range
        if (psi.language !== RobotLanguage.INSTANCE) return range
        @Suppress("UnstableApiUsage") PyPsiUtilsCore.assertValid(psi)
        val file = psi.containingFile ?: return range

        val metadata = RobotStatementMetadataCollector().process(file, file.textRange)
        file.putUserData(STATEMENT_METADATA_KEY, metadata)
        return range
    }
}

private class RobotStatementMetadataCollector : RecursiveRobotVisitor() {

    private val metadataStatements = mutableListOf<StatementMetadata>()

    private lateinit var range: TextRange

    fun process(element: PsiElement, range: TextRange): List<StatementMetadata> {
        this.range = range
        element.accept(this)
        return metadataStatements
    }

    override fun visitElement(element: PsiElement) {
        if (element.textRange.intersectsStrict(range)) {
            super.visitElement(element)
        }
    }

    override fun visitGlobalSettingStatement(o: RobotGlobalSettingStatement) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractSimpleGlobalSettingMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitKeywordCall(o: RobotKeywordCall) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractKeywordCallMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitLocalArgumentsSetting(o: RobotLocalArgumentsSetting) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractLocalArgumentsSettingMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitKeywordVariableStatement(o: RobotKeywordVariableStatement) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractKeywordVariableStatementMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitLocalSetting(o: RobotLocalSetting) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractLocalSettingMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitVariableStatement(o: RobotVariableStatement) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractSimpleVariableStatementMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitForLoopHeader(o: RobotForLoopHeader) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractForLoopHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitForLoopStructure(o: RobotForLoopStructure) {
        if (o.textRange.intersectsStrict(range)) {
            super.visitForLoopStructure(o)
            visitEndStatement(o)
        }
    }

    override fun visitWhileLoopStructure(o: RobotWhileLoopStructure) {
        if (o.textRange.intersectsStrict(range)) {
            super.visitWhileLoopStructure(o)
            visitEndStatement(o)
        }
    }

    override fun visitWhileLoopHeader(o: RobotWhileLoopHeader) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractWhileLoopHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitTemplateArguments(o: RobotTemplateArguments) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractTemplateArgumentsMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitConditionalStructure(o: RobotConditionalStructure) {
        if (o.textRange.intersectsStrict(range)) {
            super.visitConditionalStructure(o)
            visitEndStatement(o)
        }
    }

    override fun visitIfStructure(o: RobotIfStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitInlineIfStructure(o: RobotInlineIfStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitElseIfStructure(o: RobotElseIfStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitInlineElseIfStructure(o: RobotInlineElseIfStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitElseStructure(o: RobotElseStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitInlineElseStructure(o: RobotInlineElseStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitExceptionHandlingStructure(o: RobotExceptionHandlingStructure) {
        if (o.textRange.intersectsStrict(range)) {
            super.visitExceptionHandlingStructure(o)
            visitEndStatement(o)
        }
    }

    override fun visitTryStructure(o: RobotTryStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitExceptHeader(o: RobotExceptHeader) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitFinallyStructure(o: RobotFinallyStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitGroupStructure(o: RobotGroupStructure) {
        if (o.textRange.intersectsStrict(range)) {
            super.visitGroupStructure(o)
            visitEndStatement(o)
        }
    }

    override fun visitGroupHeader(o: RobotGroupHeader) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
            visitEndStatement(o)
        }
    }

    override fun visitReturnStructure(o: RobotReturnStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    override fun visitLoopControlStructure(o: RobotLoopControlStructure) {
        if (o.textRange.intersectsStrict(range)) {
            val metadataStatement = extractControlFlowHeaderMetadata(o)
            metadataStatements.add(metadataStatement)
        }
    }

    private fun visitEndStatement(o: PsiElement) {
        @Suppress("UnstableApiUsage") o.findChildByType<PsiElement>(RobotTypes.END)?.let {
            val endMetadataStatement = extractControlFlowHeaderMetadata(it)
            metadataStatements.add(endMetadataStatement)
        }
    }

    /**
     * GLOBAL SETTING: `simple_global_setting_name (parameter | positional_argument)*`
     *
     * Identification text = the global setting name.
     *
     * Wrappable argument count = number of PARAMETER + POSITIONAL_ARGUMENT children
     * (flattened through EXECUTABLE_STATEMENT wrappers).
     */
    private fun extractSimpleGlobalSettingMetadata(element: PsiElement): StatementMetadata {
        val idText = element.firstChild?.text?.trim() ?: ""
        val args = collectWrappableArguments(element, GLOBAL_SETTING_CALL_ARGUMENT_TYPES, nestedWrappableArgumentTypes = KEYWORD_CALL_ARGUMENT_NESTED_TYPES)
        return StatementMetadata(idText, args, element.textRange)
    }

    /**
     * KEYWORD_CALL: `keyword_call_name (parameter | positional_argument)*`
     *
     * Identification text = the full text of the KEYWORD_CALL_NAME child
     * (which includes optional library prefix like `LibraryName.KeywordName`).
     *
     * Wrappable argument count = number of PARAMETER + POSITIONAL_ARGUMENT children
     * (flattened through EXECUTABLE_STATEMENT wrappers).
     */
    private fun extractKeywordCallMetadata(element: PsiElement): StatementMetadata {
        @Suppress("UnstableApiUsage")
        val keywordNameNode = element.findChildByType<PsiElement>(RobotTypes.KEYWORD_CALL_NAME)
        val idText = keywordNameNode?.text?.trim() ?: element.firstChild?.text?.trim() ?: ""
        val args = collectWrappableArguments(element,
            KEYWORD_CALL_ARGUMENT_TYPES,
            takeAfterElementType = TokenSet.create(RobotTypes.KEYWORD_CALL_NAME),
            nestedWrappableArgumentTypes = KEYWORD_CALL_ARGUMENT_NESTED_TYPES)
        return StatementMetadata(idText, args, element.textRange)
    }

    /**
     * LOCAL_ARGUMENTS_SETTING: `[Arguments] param1 param2 ...`
     *
     * Identification text = `[Arguments]` (the setting marker).
     * Wrappable argument count = number of mandatory + optional parameter children.
     */
    @Suppress("UnstableApiUsage")
    private fun extractLocalArgumentsSettingMetadata(element: PsiElement): StatementMetadata {
        val settingIdNode = element.findChildByType<PsiElement>(RobotTypes.LOCAL_ARGUMENTS_SETTING_ID)
        val idText = settingIdNode?.text?.trim() ?: "[Arguments]"
        val args = collectWrappableArguments(element, LOCAL_ARGUMENTS_PARAMETER_TYPES)
        return StatementMetadata(idText, args, element.textRange)
    }

    /**
     * KEYWORD_VARIABLE_STATEMENT: `${Var}=  Some Keyword  arg1  arg2`
     *
     * Identification text = variable definitions + `=` + keyword name, e.g. `${Var}=  Some Keyword`.
     * This is built by concatenating the text of VARIABLE_DEFINITION children, any ASSIGNMENT token,
     * and the KEYWORD_CALL_NAME from the embedded KEYWORD_CALL.
     *
     * Wrappable argument count = arguments of the embedded KEYWORD_CALL.
     */
    private fun extractKeywordVariableStatementMetadata(element: PsiElement): StatementMetadata {
        var child = element.firstChild
        var idText = ""
        while (child != null && child.elementType !== RobotTypes.KEYWORD_CALL) {
            idText += child.text
            child = child.nextSibling
        }
        val args = if (child?.elementType === RobotTypes.KEYWORD_CALL) {
            idText += child.firstChild?.text ?: ""
            collectWrappableArguments(child,
                KEYWORD_CALL_ARGUMENT_TYPES,
                takeAfterElementType = TokenSet.create(RobotTypes.KEYWORD_CALL_NAME),
                nestedWrappableArgumentTypes = KEYWORD_CALL_ARGUMENT_NESTED_TYPES)
        } else emptyList()
        return StatementMetadata(idText, args, element.textRange)
    }

    /**
     * LOCAL_SETTING: `[Tags]  value1  value2` or `[Setup]  Some Keyword  arg1`
     *
     * Identification text = the setting name text (e.g. `[Tags]`).
     * Wrappable argument count = POSITIONAL_ARGUMENT + KEYWORD_CALL children.
     */
    private fun extractLocalSettingMetadata(element: PsiElement): StatementMetadata {
        @Suppress("UnstableApiUsage")
        val settingIdNode = element.findChildByType<PsiElement>(RobotTypes.LOCAL_SETTING_ID)
        val idText = settingIdNode?.text?.trim() ?: ""
        val args = collectWrappableArguments(element, LOCAL_SETTING_ARGUMENT_TYPES, nestedWrappableArgumentTypes = KEYWORD_CALL_ARGUMENT_NESTED_TYPES)
        return StatementMetadata(idText, args, element.textRange)
    }

    /**
     * SINGLE_VARIABLE_STATEMENT, INLINE_VARIABLE_STATEMENT, EMPTY_VARIABLE_STATEMENT,
     * IF_VARIABLE_STATEMENT: `${Var}=  value1  value2`
     *
     * Identification text = the variable definition text.
     * Wrappable argument count = VARIABLE_VALUE + PARAMETER children.
     */
    @Suppress("UnstableApiUsage")
    private fun extractSimpleVariableStatementMetadata(element: PsiElement): StatementMetadata {
        val varDef = element.findChildByType<PsiElement>(RobotTypes.VARIABLE_DEFINITION)
        val assignmentNode = element.findChildByType<PsiElement>(RobotTypes.ASSIGNMENT)

        val variableDefinitionText = varDef?.text?.trim() ?: ""
        val assignmentText = assignmentNode?.text?.trim() ?: ""
        val idText = variableDefinitionText + assignmentText
        val argCount = collectWrappableArguments(element, VARIABLE_STATEMENT_ARGUMENT_TYPES)
        return StatementMetadata(idText, argCount, element.textRange)
    }

    /**
     * Control flow headers (IF, ELSE IF, EXCEPT, GROUP):
     *
     * Identification text = the structural keyword text.
     * Wrappable argument count = POSITIONAL_ARGUMENT + VARIABLE_DEFINITION + CONDITIONAL_CONTENT children.
     */
    private fun extractControlFlowHeaderMetadata(element: PsiElement): StatementMetadata {
        val argCount = collectWrappableArguments(element, CONTROL_FLOW_ARGUMENT_TYPES)
        val idText = element.firstChild?.text ?: element.text
        return StatementMetadata(idText, argCount, element.textRange)
    }

    private fun extractForLoopHeaderMetadata(element: PsiElement): StatementMetadata {
        val args = collectWrappableArguments(element, CONTROL_FLOW_ARGUMENT_TYPES, RobotTokenSets.FOR_LOOP_IN_TYPES)
        var idText = ""
        var child = element.firstChild
        while (child != null) {
            if (RobotTokenSets.FOR_LOOP_IN_TYPES.contains(child.elementType)) {
                idText += child.text
                break
            }
            idText += child.text
            child = child.nextSibling
        }
        return StatementMetadata(idText, args, element.textRange)
    }

    private fun extractWhileLoopHeaderMetadata(element: PsiElement): StatementMetadata {
        val args = element.children.filter { it.elementType == RobotTypes.PARAMETER }.map { it.text }.toList()
        var idText = ""
        var child = element.firstChild
        while (child != null) {
            if (child.elementType === RobotTypes.CONDITIONAL_CONTENT) {
                idText += child.text
                break
            }
            idText += child.text
            child = child.nextSibling
        }
        return StatementMetadata(idText, args, element.textRange)
    }

    private fun extractTemplateArgumentsMetadata(element: PsiElement): StatementMetadata = StatementMetadata(element.text, emptyList(), element.textRange)

    /**
     * Collects wrappable argument children of [node] whose element type is in [argumentTypes].
     * Recurses into EXECUTABLE_STATEMENT and LITERAL_CONSTANT_VALUE (flattened types) to
     * count nested arguments.
     */
    private fun collectWrappableArguments(element: PsiElement,
                                          argumentTypes: Set<IElementType>?,
                                          takeAfterElementType: TokenSet? = null,
                                          nestedWrappableArgumentTypes: Set<IElementType> = emptySet(),
                                          wrappableArguments: MutableList<String> = mutableListOf()): List<String> {
        var child = element.firstChild
        var afterElement = takeAfterElementType == null
        while (child != null) {
            if (!afterElement && (takeAfterElementType == null || takeAfterElementType.contains(child.elementType))) {
                afterElement = true
                child = child.nextSibling
                continue
            }
            if (afterElement && argumentTypes?.contains(child.elementType) ?: true) {
                if (child.elementType in nestedWrappableArgumentTypes) {
                    collectWrappableArguments(child, null, nestedWrappableArgumentTypes = nestedWrappableArgumentTypes, wrappableArguments = wrappableArguments)
                } else {
                    val content = child.text.trim()
                    if (!content.isBlank() && child.elementType !== TokenType.WHITE_SPACE) wrappableArguments.add(content)
                }
            }
            child = child.nextSibling
        }
        return wrappableArguments
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Argument type sets for counting wrappable arguments per statement type
// ─────────────────────────────────────────────────────────────────────────────

/**
 * For Global settings: wrappable arguments are PARAMETER, POSITIONAL_ARGUMENT, KEYWORD_CALL and LITERAL_CONSTANT children.
 */
private val GLOBAL_SETTING_CALL_ARGUMENT_TYPES: Set<IElementType> = setOf(RobotTypes.PARAMETER,
    RobotTypes.POSITIONAL_ARGUMENT,
    RobotTypes.KEYWORD_CALL,
    RobotTypes.LITERAL_CONSTANT_VALUE,
    RobotTypes.LITERAL_CONSTANT,
    RobotTypes.IMPORT_ARGUMENT,
    RobotTypes.WITH_NAME,
    RobotTypes.NEW_LIBRARY_NAME)

/**
 * For KEYWORD_CALL: wrappable arguments are PARAMETER, POSITIONAL_ARGUMENT and KEYWORD_CALL children.
 */
private val KEYWORD_CALL_ARGUMENT_TYPES: Set<IElementType> =
    setOf(RobotTypes.PARAMETER, RobotTypes.POSITIONAL_ARGUMENT, RobotTypes.KEYWORD_CALL, RobotTypes.KEYWORD_CALL_NAME, RobotTypes.ELSE_IF, RobotTypes.PYTHON_EXPRESSION_CONTENT, RobotTypes.ELSE)
private val KEYWORD_CALL_ARGUMENT_NESTED_TYPES: Set<IElementType> =
    setOf(RobotTypes.KEYWORD_CALL, RobotTypes.POSITIONAL_ARGUMENT, RobotTypes.INLINE_ELSE_IF_STRUCTURE, RobotTypes.INLINE_ELSE_STRUCTURE)

/**
 * For LOCAL_ARGUMENTS_SETTING: wrappable arguments are the mandatory and optional parameter children.
 */
private val LOCAL_ARGUMENTS_PARAMETER_TYPES: Set<IElementType> =
    setOf(RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_MANDATORY, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL, RobotTypes.LOCAL_ARGUMENTS_SETTING_PARAMETER)

/**
 * For LOCAL_SETTING: wrappable arguments are positional arguments and embedded keyword calls.
 */
private val LOCAL_SETTING_ARGUMENT_TYPES: Set<IElementType> = setOf(RobotTypes.POSITIONAL_ARGUMENT, RobotTypes.KEYWORD_CALL)

/**
 * For variable statements (SINGLE_VARIABLE_STATEMENT, etc.): wrappable arguments are
 * variable values and parameters.
 */
private val VARIABLE_STATEMENT_ARGUMENT_TYPES: Set<IElementType> = setOf(RobotTypes.VARIABLE_VALUE, RobotTypes.PARAMETER)

/**
 * For control flow headers: wrappable arguments are positional arguments, variable
 * definitions, and conditional content.
 */
private val CONTROL_FLOW_ARGUMENT_TYPES: Set<IElementType> = setOf(RobotTypes.POSITIONAL_ARGUMENT, RobotTypes.VARIABLE_DEFINITION, RobotTypes.CONDITIONAL_CONTENT, RobotTypes.PARAMETER)

/**
 * Set of Robot Framework element types whose AST nodes may contain continuation markers
 * ("...") or need to be identified as a statement in a line. The [RobotPreFormatProcessor]
 * scopes its continuation scanning to within each of these nodes, preventing cross-statement
 * collapsing.
 *
 * This includes all statement types that can span multiple lines using "...":
 * - Keyword calls (with or without variable assignments)
 * - Local settings ([Arguments], [Documentation], etc.)
 * - Variable definitions in the *** Variables *** section
 * But also everything else that represents a single statement in a line
 */
private val STATEMENT_TYPES: Set<IElementType> = setOf(RobotTypes.KEYWORD_CALL,
    RobotTypes.LOCAL_ARGUMENTS_SETTING,
    RobotTypes.LOCAL_SETTING,
    RobotTypes.KEYWORD_VARIABLE_STATEMENT,
    RobotTypes.SINGLE_VARIABLE_STATEMENT,
    RobotTypes.INLINE_VARIABLE_STATEMENT,
    RobotTypes.EMPTY_VARIABLE_STATEMENT,
    RobotTypes.IF_VARIABLE_STATEMENT,
    RobotTypes.EXECUTABLE_STATEMENT,
    RobotTypes.DOCUMENTATION_STATEMENT_GLOBAL_SETTING,
    RobotTypes.TAGS_STATEMENT_GLOBAL_SETTING,
    RobotTypes.TEMPLATE_STATEMENTS_GLOBAL_SETTING,
    RobotTypes.METADATA_STATEMENT_GLOBAL_SETTING,
    RobotTypes.SUITE_NAME_STATEMENT_GLOBAL_SETTING,
    RobotTypes.TIMEOUT_STATEMENTS_GLOBAL_SETTING,
    RobotTypes.UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING,
    RobotTypes.VARIABLES_IMPORT_GLOBAL_SETTING,
    RobotTypes.LIBRARY_IMPORT_GLOBAL_SETTING,
    RobotTypes.RESOURCE_IMPORT_GLOBAL_SETTING,
    RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING,
    RobotTypes.TEMPLATE_ARGUMENTS, // Control flow headers can also have continuations (e.g. FOR with many items)
    RobotTypes.FOR_LOOP_HEADER,
    RobotTypes.WHILE_LOOP_HEADER,
    RobotTypes.IF,
    RobotTypes.ELSE_IF,
    RobotTypes.ELSE,
    RobotTypes.TRY,
    RobotTypes.EXCEPT,
    RobotTypes.FINALLY,
    RobotTypes.GROUP_HEADER,
    RobotTypes.END,
    RobotTypes.BREAK,
    RobotTypes.CONTINUE,
    RobotTypes.RETURN)
