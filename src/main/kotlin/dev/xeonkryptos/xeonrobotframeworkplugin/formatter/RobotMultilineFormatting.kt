package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.CodeStyle
import com.intellij.lang.ASTNode
import com.intellij.lang.tree.util.children
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.removeUserData
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

// ─────────────────────────────────────────────────────────────────────────────
// Shared data model for Pre→Post processor communication
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Metadata collected from the AST **before** continuation markers are collapsed.
 *
 * This information is gathered by the [RobotPreFormatProcessor] while the AST is still
 * intact and attached to the [PsiFile] via [STATEMENT_METADATA_KEY]. The
 * [RobotPostFormatProcessor] reads it back to accurately determine statement boundaries
 * even when the AST is broken after wrapping.
 *
 * @property identificationText A textual fingerprint that uniquely identifies the
 *   statement's "head" in the document text. For keyword calls this is the keyword name
 *   (optionally prefixed with the library name), for `[Arguments]` it is `[Arguments]`,
 *   for variable statements it is the variable assignment prefix followed by the keyword
 *   name, etc. This text can be searched for in the formatted document to locate the
 *   statement's starting position.
 * @property wrappableArgumentCount The number of arguments/parameters that the formatter
 *   may distribute across multiple lines. After this many continuation lines have been
 *   emitted for a statement, the PostFormatProcessor knows that the next indented line
 *   must belong to a **new** statement.
 */
data class StatementMetadata(val identificationText: String, val wrappableArguments: List<String>) {
    val wrappableArgumentCount: Int = wrappableArguments.size
    val normalizedIdentificationText: String = identificationText.replace(WHITESPACE_REGEX, "").trim()
}

/**
 * [Key] used to attach a list of [StatementMetadata] to the [PsiFile] as user data.
 * Written by [RobotPreFormatProcessor], read by [RobotPostFormatProcessor].
 */
val STATEMENT_METADATA_KEY: Key<List<StatementMetadata>> = Key.create("ROBOT_STATEMENT_METADATA")

val WHITESPACE_REGEX = Regex("\\s*")

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
        val file = psi.containingFile ?: return range
        if (file.language !== RobotLanguage.INSTANCE) return range

        val customSettings = CodeStyle.getCustomSettings(file, RobotCodeStyleSettings::class.java)

        val currentRange = if (element.textRange !in range) element.textRange else range

        // ── Collect statement metadata from the intact AST ──
        // This MUST happen before collapsing continuations, because after collapsing
        // the AST will be reparsed and statement structure may differ.
        val metadata = collectStatementMetadata(element, currentRange, customSettings)

        val document = file.fileDocument // Use the AST to find statement boundaries, then collect continuation sequences
        // only within each statement's text range. This prevents cross-statement collapsing.
        val sequences = collectContinuationSequencesFromAST(element, document, currentRange) // Even if there's nothing to collapse, attach metadata (wrapping may still
        // apply to single-line statements that are too long).
        file.putUserData(STATEMENT_METADATA_KEY, metadata)
        if (sequences.isEmpty()) return range

        var totalDelta = 0
        val commonSettings = CodeStyle.getLanguageSettings(file, RobotLanguage.INSTANCE)
        if (!commonSettings.KEEP_LINE_BREAKS) {
            for (seq in sequences.sortedByDescending { it.startOffset }) {
                val originalLength = seq.endOffset - seq.startOffset
                document.replaceString(seq.startOffset, seq.endOffset, GlobalConstants.SUPER_SPACE)
                totalDelta += originalLength - GlobalConstants.SUPER_SPACE.length
            }
            PsiDocumentManager.getInstance(file.project).commitDocument(document)
        }

        file.putUserData(STATEMENT_METADATA_KEY, metadata)
        return if (totalDelta > 0) currentRange.grown(-totalDelta) else currentRange
    }

    /**
     * Traverses the AST to find all statements that contain continuation markers, then
     * collects collapsible continuation sequences scoped to each statement.
     *
     * This is the key improvement over pure text scanning: by using the AST to determine
     * statement boundaries, we guarantee that continuations from **different** statements
     * are never collapsed together. For example:
     * ```
     *     &{Var1}=  Some Keyword
     *     ...    param1=X
     *     ...    param2=E
     *     &{Var2}=  Some Keyword
     *     ...    param2=Y
     * ```
     * The `...` on lines 2-3 belong to the `&{Var1}=` statement, and the `...` on line 5
     * belongs to `&{Var2}=`. Without AST scoping, the text scanner might collapse across
     * the `&{Var2}=` boundary.
     *
     * Within each statement, the chain-based inline-comment check is applied: if any
     * continuation line contains an inline comment, the entire statement's continuations
     * are left intact.
     */
    private fun collectContinuationSequencesFromAST(root: ASTNode, document: Document, range: TextRange): List<ContinuationSequence> {
        val text = document.text
        val result = mutableListOf<ContinuationSequence>()

        val statementRanges = mutableListOf<TextRange>()
        collectStatementTextRanges(root, range, statementRanges)

        for (stmtRange in statementRanges) {
            val seqsForStatement = collectContinuationSequencesInRange(text, stmtRange, range)
            if (seqsForStatement.isEmpty()) continue

            var previousStatementWithComment = false
            for (sequence in seqsForStatement) {
                if (!previousStatementWithComment) {
                    result.add(sequence)
                }
                previousStatementWithComment = sequence.hasInlineComment
            }
        }

        return result
    }

    /**
     * Recursively finds all statement-level AST nodes and collects their text ranges.
     * These ranges define the boundaries within which continuation markers can be collapsed.
     */
    private fun collectStatementTextRanges(node: ASTNode, range: TextRange, result: MutableList<TextRange>) {
        val elementType = node.elementType
        if (elementType in STATEMENT_TYPES) {
            val nodeRange = node.textRange
            if (nodeRange.intersects(range)) {
                result.add(nodeRange)
            } // Don't recurse into children of matching statements — continuations within
            // are handled by the text scanner scoped to this range.
            return
        }
        var child = node.firstChildNode
        while (child != null) {
            collectStatementTextRanges(child, range, result)
            child = child.treeNext
        }
    }

    /**
     * Scans the text within [statementRange] for continuation marker sequences.
     * Only sequences whose newline falls within the overall [formattingRange] are included.
     *
     * Returns a list of [ContinuationSequence] objects, each tagged with whether the
     * continuation line's content contains an inline comment.
     */
    private fun collectContinuationSequencesInRange(text: String, statementRange: TextRange, formattingRange: TextRange): List<ContinuationSequence> {
        val sequences = mutableListOf<ContinuationSequence>()
        val scanEnd = minOf(statementRange.endOffset, formattingRange.endOffset, text.length)

        var searchFrom = statementRange.startOffset
        while (searchFrom < scanEnd) {
            val dotsIdx = text.indexOf(GlobalConstants.CONTINUATION, searchFrom)
            if (dotsIdx !in 0..<scanEnd) break

            // Walk backwards to find newline + indent
            var seqStart = dotsIdx
            while (seqStart > 0) {
                val c = text[seqStart - 1]
                if (c == ' ' || c == '\t' || c == '\u00A0') seqStart--
                else break
            }
            val hasNewlineBefore = seqStart > 0 && (text[seqStart - 1] == '\n' || text[seqStart - 1] == '\r')
            if (!hasNewlineBefore) {
                searchFrom = dotsIdx + GlobalConstants.CONTINUATION.length
                continue
            }
            seqStart-- // include \n
            if (seqStart > 0 && text[seqStart - 1] == '\r' && text[seqStart] == '\n') {
                seqStart-- // include \r\n
            }

            // Only process if the newline is within the formatting range
            if (seqStart < formattingRange.startOffset) {
                searchFrom = dotsIdx + GlobalConstants.CONTINUATION.length
                continue
            }

            // Check preceding line for inline comment
            if (precedingLineHasInlineComment(text, seqStart)) {
                searchFrom = dotsIdx + GlobalConstants.CONTINUATION.length
                continue
            }

            // Validate "..." is followed by separator
            val afterDots = dotsIdx + GlobalConstants.CONTINUATION.length
            if (afterDots >= text.length) {
                sequences.add(ContinuationSequence(seqStart, afterDots, hasInlineComment = false))
                break
            }
            val charAfterDots = text[afterDots]
            val isValid = charAfterDots == '\t' || charAfterDots == '\n' || charAfterDots == '\r' || (charAfterDots == ' ' && afterDots + 1 < text.length && text[afterDots + 1] == ' ')
            if (!isValid) {
                searchFrom = afterDots
                continue
            }

            // Consume separator whitespace after "..."
            var seqEnd = afterDots
            if (charAfterDots != '\n' && charAfterDots != '\r') {
                while (seqEnd < text.length) {
                    val c = text[seqEnd]
                    if (c == ' ' || c == '\t' || c == '\u00A0') seqEnd++
                    else break
                }
            }

            // Check if the rest of the current line has a comment
            val hasComment = currentLineHasInlineComment(text, seqEnd)
            sequences.add(ContinuationSequence(seqStart, seqEnd, hasInlineComment = hasComment))
            searchFrom = seqEnd
        }

        return sequences
    }

    private fun precedingLineHasInlineComment(text: String, newlineOffset: Int): Boolean {
        var lineStart = newlineOffset
        while (lineStart > 0) {
            val c = text[lineStart - 1]
            if (c == '\n' || c == '\r') break
            lineStart--
        }
        val lineContent = text.substring(lineStart, newlineOffset)
        return containsRobotComment(lineContent)
    }

    private fun currentLineHasInlineComment(text: String, contentStart: Int): Boolean {
        var lineEnd = contentStart
        while (lineEnd < text.length && text[lineEnd] != '\n' && text[lineEnd] != '\r') {
            lineEnd++
        }
        val lineContent = text.substring(contentStart, lineEnd)
        return containsRobotComment(lineContent)
    }

    private fun containsRobotComment(lineContent: String): Boolean {
        val hashIdx = lineContent.indexOf('#')
        if (hashIdx < 0) return false
        if (hashIdx == 0) return true
        if (hashIdx >= 2 && lineContent[hashIdx - 1] == ' ' && lineContent[hashIdx - 2] == ' ') return true
        if (lineContent[hashIdx - 1] == '\t') return true
        return false
    }

    override fun changesWhitespacesOnly(): Boolean = true

    private data class ContinuationSequence(val startOffset: Int, val endOffset: Int, val hasInlineComment: Boolean = false)

    /**
     * Collects [StatementMetadata] for all collapsible statements within [range].
     *
     * This traverses the AST (which is still intact at this point) and for each
     * collapsible statement node extracts:
     * - An identification text (the "head" of the statement)
     * - The count of wrappable arguments
     */
    private fun collectStatementMetadata(root: ASTNode, range: TextRange, customSettings: RobotCodeStyleSettings): List<StatementMetadata> {
        val result = mutableListOf<StatementMetadata>()
        collectMetadataRecursive(root, range, customSettings, result)
        return result
    }

    private fun collectMetadataRecursive(node: ASTNode, range: TextRange, customSettings: RobotCodeStyleSettings, result: MutableList<StatementMetadata>) {
        val elementType = node.elementType
        if (elementType in STATEMENT_TYPES) {
            val nodeRange = node.textRange
            if (range.contains(nodeRange)) {
                val metadata = extractMetadata(node, customSettings)
                if (metadata != null) {
                    result.add(metadata)
                }
            }
        } else {
            var child = node.firstChildNode
            while (child != null) {
                collectMetadataRecursive(child, range, customSettings, result)
                child = child.treeNext
            }
        }
    }

    /**
     * Extracts [StatementMetadata] from a single AST node.
     *
     * The identification text and argument count depend on the node's element type:
     *
     * | Element type                    | Identification text               | Wrappable arguments                   |
     * |---------------------------------|-----------------------------------|---------------------------------------|
     * | KEYWORD_CALL                    | keyword name (with library prefix)| PARAMETER + POSITIONAL_ARGUMENT count |
     * | LOCAL_ARGUMENTS_SETTING         | `[Arguments]`                     | PARAMETER_MANDATORY + _OPTIONAL count |
     * | KEYWORD_VARIABLE_STATEMENT      | var defs + `=` + keyword name     | args of the embedded keyword call     |
     * | LOCAL_SETTING                   | setting name (e.g. `[Tags]`)      | POSITIONAL_ARGUMENT count             |
     * | EXECUTABLE_STATEMENT            | delegates to first child          | delegates to first child              |
     * | FOR_LOOP_HEADER, IF, ELSE_IF, …| structural keyword text           | POSITIONAL_ARGUMENT count             |
     * | *_VARIABLE_STATEMENT            | variable name                     | VARIABLE_VALUE / PARAMETER count      |
     */
    private fun extractMetadata(node: ASTNode, customSettings: RobotCodeStyleSettings): StatementMetadata? {
        return when (node.elementType) {
            RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING,
            RobotTypes.DOCUMENTATION_STATEMENT_GLOBAL_SETTING,
            RobotTypes.SUITE_NAME_STATEMENT_GLOBAL_SETTING,
            RobotTypes.LIBRARY_IMPORT_GLOBAL_SETTING,
            RobotTypes.METADATA_STATEMENT_GLOBAL_SETTING,
            RobotTypes.TAGS_STATEMENT_GLOBAL_SETTING,
            RobotTypes.TEMPLATE_STATEMENTS_GLOBAL_SETTING,
            RobotTypes.VARIABLES_IMPORT_GLOBAL_SETTING,
            RobotTypes.TIMEOUT_STATEMENTS_GLOBAL_SETTING,
            RobotTypes.RESOURCE_IMPORT_GLOBAL_SETTING,
            RobotTypes.UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING -> extractSimpleGlobalSettingMetadata(node)

            RobotTypes.KEYWORD_CALL -> extractKeywordCallMetadata(node)
            RobotTypes.LOCAL_ARGUMENTS_SETTING -> extractLocalArgumentsSettingMetadata(node)
            RobotTypes.KEYWORD_VARIABLE_STATEMENT -> extractKeywordVariableStatementMetadata(node)
            RobotTypes.LOCAL_SETTING -> extractLocalSettingMetadata(node)
            RobotTypes.EXECUTABLE_STATEMENT -> extractExecutableStatementMetadata(node, customSettings)
            RobotTypes.SINGLE_VARIABLE_STATEMENT -> extractSimpleVariableStatementMetadata(node)
            RobotTypes.INLINE_VARIABLE_STATEMENT -> extractSimpleVariableStatementMetadata(node)
            RobotTypes.EMPTY_VARIABLE_STATEMENT -> extractSimpleVariableStatementMetadata(node)
            RobotTypes.IF_VARIABLE_STATEMENT -> extractSimpleVariableStatementMetadata(node)
            RobotTypes.FOR_LOOP_HEADER -> extractForLoopHeaderMetadata(node)
            RobotTypes.WHILE_LOOP_HEADER -> extractWhileLoopHeaderMetadata(node)

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
            RobotTypes.RETURN -> extractControlFlowHeaderMetadata(node)

            RobotTypes.TEMPLATE_ARGUMENTS -> extractTemplateArgumentsMetadata(node)
            else -> null
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
    private fun extractSimpleGlobalSettingMetadata(node: ASTNode): StatementMetadata {
        val idText = node.firstChildNode?.text?.trim() ?: ""
        val args = collectWrappableArguments(node, GLOBAL_SETTING_CALL_ARGUMENT_TYPES, nestedWrappableArgumentTypes = KEYWORD_CALL_ARGUMENT_NESTED_TYPES)
        return StatementMetadata(idText, args)
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
    private fun extractKeywordCallMetadata(node: ASTNode): StatementMetadata {
        val keywordNameNode = findChildOfType(node, RobotTypes.KEYWORD_CALL_NAME)
        val idText = keywordNameNode?.text?.trim() ?: node.firstChildNode?.text?.trim() ?: ""
        val args = collectWrappableArguments(node,
            KEYWORD_CALL_ARGUMENT_TYPES,
            takeAfterElementType = TokenSet.create(RobotTypes.KEYWORD_CALL_NAME),
            nestedWrappableArgumentTypes = KEYWORD_CALL_ARGUMENT_NESTED_TYPES)
        return StatementMetadata(idText, args)
    }

    /**
     * LOCAL_ARGUMENTS_SETTING: `[Arguments] param1 param2 ...`
     *
     * Identification text = `[Arguments]` (the setting marker).
     * Wrappable argument count = number of mandatory + optional parameter children.
     */
    private fun extractLocalArgumentsSettingMetadata(node: ASTNode): StatementMetadata {
        val settingIdNode = findChildOfType(node, RobotTypes.LOCAL_ARGUMENTS_SETTING_ID)
        val idText = settingIdNode?.text?.trim() ?: "[Arguments]"
        val args = collectWrappableArguments(node, LOCAL_ARGUMENTS_PARAMETER_TYPES)
        return StatementMetadata(idText, args)
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
    private fun extractKeywordVariableStatementMetadata(node: ASTNode): StatementMetadata {
        var child = node.firstChildNode
        var idText = ""
        while (child != null && child.elementType !== RobotTypes.KEYWORD_CALL) {
            idText += child.text
            child = child.treeNext
        }
        val args = if (child?.elementType === RobotTypes.KEYWORD_CALL) {
            idText += child.firstChildNode?.text ?: ""
            collectWrappableArguments(child,
                KEYWORD_CALL_ARGUMENT_TYPES,
                takeAfterElementType = TokenSet.create(RobotTypes.KEYWORD_CALL_NAME),
                nestedWrappableArgumentTypes = KEYWORD_CALL_ARGUMENT_NESTED_TYPES)
        } else emptyList()
        return StatementMetadata(idText, args)
    }

    /**
     * LOCAL_SETTING: `[Tags]  value1  value2` or `[Setup]  Some Keyword  arg1`
     *
     * Identification text = the setting name text (e.g. `[Tags]`).
     * Wrappable argument count = POSITIONAL_ARGUMENT + KEYWORD_CALL children.
     */
    private fun extractLocalSettingMetadata(node: ASTNode): StatementMetadata {
        val settingIdNode = findChildOfType(node, RobotTypes.LOCAL_SETTING_ID)
        val idText = settingIdNode?.text?.trim() ?: ""
        val args = collectWrappableArguments(node, LOCAL_SETTING_ARGUMENT_TYPES, nestedWrappableArgumentTypes = KEYWORD_CALL_ARGUMENT_NESTED_TYPES)
        return StatementMetadata(idText, args)
    }

    /**
     * EXECUTABLE_STATEMENT: a wrapper that contains exactly one "real" statement child.
     * Delegates to that child.
     */
    private fun extractExecutableStatementMetadata(node: ASTNode, customSettings: RobotCodeStyleSettings): StatementMetadata? {
        var child = node.firstChildNode
        while (child != null) {
            if (child.elementType !== TokenType.WHITE_SPACE && child.elementType !== RobotTypes.EOL && child.elementType !== RobotTypes.EOS) {
                return extractMetadata(child, customSettings)
            }
            child = child.treeNext
        }
        return null
    }

    /**
     * SINGLE_VARIABLE_STATEMENT, INLINE_VARIABLE_STATEMENT, EMPTY_VARIABLE_STATEMENT,
     * IF_VARIABLE_STATEMENT: `${Var}=  value1  value2`
     *
     * Identification text = the variable definition text.
     * Wrappable argument count = VARIABLE_VALUE + PARAMETER children.
     */
    private fun extractSimpleVariableStatementMetadata(node: ASTNode): StatementMetadata {
        val varDef = findChildOfType(node, RobotTypes.VARIABLE_DEFINITION)
        val assignmentNode = findChildOfType(node, RobotTypes.ASSIGNMENT)

        val variableDefinitionText = varDef?.text?.trim() ?: ""
        val assignmentText = assignmentNode?.text?.trim() ?: ""
        val idText = variableDefinitionText + assignmentText
        val argCount = collectWrappableArguments(node, VARIABLE_STATEMENT_ARGUMENT_TYPES)
        return StatementMetadata(idText, argCount)
    }

    /**
     * Control flow headers (IF, ELSE IF, EXCEPT, GROUP):
     *
     * Identification text = the structural keyword text.
     * Wrappable argument count = POSITIONAL_ARGUMENT + VARIABLE_DEFINITION + CONDITIONAL_CONTENT children.
     */
    private fun extractControlFlowHeaderMetadata(node: ASTNode): StatementMetadata {
        val argCount = collectWrappableArguments(node, CONTROL_FLOW_ARGUMENT_TYPES)
        val idText = node.text
        return StatementMetadata(idText, argCount)
    }

    private fun extractForLoopHeaderMetadata(node: ASTNode): StatementMetadata {
        val args = collectWrappableArguments(node, CONTROL_FLOW_ARGUMENT_TYPES, RobotTokenSets.FOR_LOOP_IN_TYPES)
        var idText = ""
        var child = node.firstChildNode
        while (child != null) {
            if (RobotTokenSets.FOR_LOOP_IN_TYPES.contains(child.elementType)) {
                idText += child.text
                break
            }
            idText += child.text
            child = child.treeNext
        }
        return StatementMetadata(idText, args)
    }

    private fun extractWhileLoopHeaderMetadata(node: ASTNode): StatementMetadata {
        val args = node.children().filter { it.elementType == RobotTypes.PARAMETER }.map { it.text }.toList()
        var idText = ""
        var child = node.firstChildNode
        while (child != null) {
            if (child.elementType === RobotTypes.CONDITIONAL_CONTENT) {
                idText += child.text
                break
            }
            idText += child.text
            child = child.treeNext
        }
        return StatementMetadata(idText, args)
    }

    private fun extractTemplateArgumentsMetadata(node: ASTNode): StatementMetadata = StatementMetadata(node.text, emptyList())

    /**
     * Collects wrappable argument children of [node] whose element type is in [argumentTypes].
     * Recurses into EXECUTABLE_STATEMENT and LITERAL_CONSTANT_VALUE (flattened types) to
     * count nested arguments.
     */
    private fun collectWrappableArguments(node: ASTNode,
                                          argumentTypes: Set<IElementType>?,
                                          takeAfterElementType: TokenSet? = null,
                                          nestedWrappableArgumentTypes: Set<IElementType> = emptySet(),
                                          wrappableArguments: MutableList<String> = mutableListOf()): List<String> {
        var child = node.firstChildNode
        var afterElement = takeAfterElementType == null
        while (child != null) {
            if (!afterElement && (takeAfterElementType == null || takeAfterElementType.contains(child.elementType))) {
                afterElement = true
                child = child.treeNext
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
            child = child.treeNext
        }
        return wrappableArguments
    }

    private fun findChildOfType(node: ASTNode, type: IElementType): ASTNode? {
        var child = node.firstChildNode
        while (child != null) {
            if (child.elementType === type) return child
            child = child.treeNext
        }
        return null
    }
}

/**
 * PostFormatProcessor that inserts continuation markers ("...") after the formatter has
 * wrapped lines.
 *
 * After the IntelliJ formatting engine wraps long lines via [com.intellij.formatting.Wrap] objects configured in
 * [RobotBlock], it creates new lines with appropriate indentation. However, the formatting
 * engine only manipulates whitespace — it cannot insert new semantic tokens like the Robot
 * Framework continuation marker ("..."). Without these markers, the wrapped code is
 * syntactically invalid and the parser will produce errors.
 *
 * This processor scans the formatted document line-by-line and identifies continuation lines
 * that were created by the wrapping process. It then inserts `"...  "` (continuation marker
 * followed by a configurable separator) at the beginning of the content on those lines.
 *
 * **Detection strategy — PSI-aware line scanning:**
 *
 * Rather than relying purely on textual heuristics, this processor uses the PSI tree
 * (which may contain parse errors after wrapping) to determine the context of each line.
 * Specifically, it finds all KEYWORD_CALL and LOCAL_ARGUMENTS_SETTING elements, determines
 * which line they start on, and then marks all subsequent indented lines that belong to
 * the same (now-broken) statement as continuation lines.
 *
 * A line is identified as needing a continuation marker if ALL of the following are true:
 * 1. It is indented (starts with whitespace)
 * 2. It does NOT already have a continuation marker ("...")
 * 3. It does NOT start a section header ("***")
 * 4. It is NOT a top-level definition (keyword/test case name at column 0)
 * 5. It is NOT a structural keyword line (FOR, IF, END, \[Arguments], etc.)
 * 6. It is NOT a comment line
 * 7. It is NOT an empty/blank line
 * 8. The preceding content line was a wrappable statement start or continuation
 *
 * The continuation marker is inserted directly before the first non-whitespace character
 * on the line, with the configured separator (at least 2 spaces) between the marker and
 * the content, producing valid Robot Framework multiline syntax:
 * ```
 *     ...    argument_value
 * ```
 */
class RobotPostFormatProcessor : PostFormatProcessor {

    private val sectionNameNormalizationRegex = Regex("[\\s*]")

    override fun processElement(source: PsiElement, settings: CodeStyleSettings): PsiElement {
        val psiFile = source.containingFile
        val textRange = processText(psiFile, source.textRange, settings)
        return psiFile.findElementAt(textRange.startOffset) ?: source
    }

    override fun processText(source: PsiFile, rangeToReformat: TextRange, settings: CodeStyleSettings): TextRange {
        if (source.language !== RobotLanguage.INSTANCE) return rangeToReformat

        val commonSettings = settings.getCommonSettings(RobotLanguage.INSTANCE)
        val customSettings = settings.getCustomSettings(RobotCodeStyleSettings::class.java)

        // Read statement metadata collected by the PreFormatProcessor. Without it, correct insertion locations can't be identified
        val statementMetadata = source.getUserData(STATEMENT_METADATA_KEY) ?: return rangeToReformat

        val document = source.fileDocument // Remove dangling continuation markers ("..." lines with no arguments) that were
        // left behind after the PreFormatProcessor preserved a chain (e.g. due to inline
        // comments) and the formatter then wrapped arguments onto new lines.
        val removedChars = removeDanglingContinuationMarkers(document, rangeToReformat)
        val range = if (removedChars > 0) TextRange(rangeToReformat.startOffset, (rangeToReformat.endOffset - removedChars).coerceAtLeast(rangeToReformat.startOffset))
        else rangeToReformat

        val lineProcessors = collectContinuationInsertions(document, range, commonSettings, statementMetadata)
        if (lineProcessors.isEmpty()) return range

        // Insert in reverse document order to keep earlier offsets stable
        val totalDelta = lineProcessors.sortedDescending().sumOf { it.processLine(document, commonSettings, customSettings) }

        // Clean up user data after use
        source.removeUserData(STATEMENT_METADATA_KEY)

        val psiDocumentManager = PsiDocumentManager.getInstance(source.project)
        psiDocumentManager.commitDocument(document)

        return if (totalDelta > 0) range.grown(totalDelta) else range
    }

    /**
     * Removes "dangling" continuation marker lines — lines that consist solely of
     * optional whitespace and `"..."` with no actual content after the marker.
     *
     * These arise when the [RobotPreFormatProcessor] preserves a continuation chain
     * (e.g. because it contains an inline comment) and the formatter subsequently wraps
     * the arguments onto new lines. The old `"..."` marker remains on its original line,
     * but the argument that was after it has been moved to the next line, leaving behind
     * an empty continuation line like:
     * ```
     *     argument
     *         ...
     *     argumentMovedFromTopLine
     * ```
     * Such a line needs to be removed to correctly handle continuation insertion after
     * the formatter has done its stuff. It would be a hassle to not only add new continuation
     * markers where required, but also move arguments onto a previous line because of an
     * already existing marker.
     *
     * This method scans the document for such lines and removes them entirely, including
     * the preceding newline character, so no blank lines are left behind.
     *
     * @return the total number of characters removed from the document
     */
    private fun removeDanglingContinuationMarkers(document: Document, range: TextRange): Int {
        val text = document.text
        if (text.isEmpty()) return 0

        val safeEndOffset = minOf(range.endOffset, text.length)
        if (range.startOffset >= safeEndOffset) return 0

        val startLine = document.getLineNumber(range.startOffset)
        val endLine = document.getLineNumber((safeEndOffset - 1).coerceAtLeast(0))

        // First pass: collect line indices of dangling continuation markers
        val danglingLines = mutableListOf<Int>()
        for (lineIdx in startLine..endLine) {
            val lineStart = document.getLineStartOffset(lineIdx)
            val lineEnd = document.getLineEndOffset(lineIdx)
            val lineText = text.substring(lineStart, lineEnd)
            val trimmed = lineText.trim()

            // A dangling marker is a line whose only non-whitespace content is "..."
            if (trimmed == GlobalConstants.CONTINUATION) {
                danglingLines.add(lineIdx)
            }
        }

        if (danglingLines.isEmpty()) return 0

        // Second pass: delete dangling lines in reverse order to keep offsets stable.
        // For each line, delete from the end of the preceding line (to consume the newline)
        // through the end of the dangling line. For the first line in the document, delete
        // from the line start through the start of the next line (to consume the trailing newline).
        var totalRemoved = 0
        for (lineIdx in danglingLines.asReversed()) {
            val lineStart = document.getLineStartOffset(lineIdx)
            val lineEnd = document.getLineEndOffset(lineIdx)

            val deleteFrom: Int
            val deleteTo: Int

            if (lineStart > 0) { // Delete the newline before this line + the entire line content
                // This effectively removes the line without leaving a blank line
                deleteFrom = lineStart - 1 // include the \n (or last char of \r\n)
                deleteTo = lineEnd // Handle \r\n: if the character before \n is \r, include it too
                val adjustedFrom = if (deleteFrom > 0 && text[deleteFrom] == '\n' && text[deleteFrom - 1] == '\r') {
                    deleteFrom - 1
                } else {
                    deleteFrom
                }
                document.deleteString(adjustedFrom, deleteTo)
                totalRemoved += deleteTo - adjustedFrom
            } else { // First line in document — delete the line content + trailing newline
                deleteTo = if (lineEnd < text.length) lineEnd + 1 else lineEnd
                document.deleteString(lineStart, deleteTo)
                totalRemoved += deleteTo - lineStart
            }
        }

        return totalRemoved
    }

    /**
     * Scans the document line-by-line within [range] and returns a list of offsets where a
     * continuation marker must be inserted.
     *
     * When [statementMetadata] is available (from the PreFormatProcessor), it is used to:
     * 1. Identify wrappable statement start lines by matching the identification text
     * 2. Limit the number of continuation lines per statement to [StatementMetadata.wrappableArgumentCount]
     *
     * This ensures that:
     * - Continuation markers are only inserted for lines that truly belong to a wrappable statement
     * - After the expected number of arguments, the next indented line is treated as a new statement
     * - Future features (like "first argument on next line") can be accommodated without
     *   breaking the boundary detection
     */
    private fun collectContinuationInsertions(document: Document, range: TextRange, commonSettings: CommonCodeStyleSettings, statementMetadata: List<StatementMetadata>): List<LineProcessor> {
        val text = document.text
        if (text.isEmpty()) return emptyList()

        val lineProcessors = mutableListOf<LineProcessor>()
        val statementMetadataCopy = statementMetadata.toMutableList()

        val safeEndOffset = minOf(range.endOffset, text.length).coerceAtLeast(0)
        if (range.startOffset >= safeEndOffset) return emptyList()

        val startLine = document.getLineNumber(range.startOffset)
        val endLine = document.getLineNumber((safeEndOffset - 1).coerceAtLeast(0))

        var insideWrappableStatement = false
        var statementIndent = -1
        var currentMetadata: StatementMetadata? = null
        var identifiedArgumentsCount = 0
        var metadataSection = false

        for (lineIdx in startLine..endLine) {
            if (currentMetadata == null && statementMetadataCopy.isEmpty()) break

            val lineStart = document.getLineStartOffset(lineIdx)
            val lineEnd = document.getLineEndOffset(lineIdx)
            val lineText = text.substring(lineStart, lineEnd)
            val trimmed = lineText.trimStart()
            val leadingWs = lineText.length - trimmed.length

            // --- Empty line: breaks any ongoing statement ---
            if (trimmed.isEmpty()) {
                insideWrappableStatement = false
                currentMetadata = null
                identifiedArgumentsCount = 0
                continue
            }

            // --- Section header (e.g. "*** Keywords ***"): always resets. Settings section is remembered to look for wrapping keywords in it being place at the start of the line ---
            if (lineText.startsWith("*")) {
                val sectionName = trimmed.replace(sectionNameNormalizationRegex, "")
                metadataSection = isMetadataSection(sectionName)
                insideWrappableStatement = false
                currentMetadata = null
                identifiedArgumentsCount = 0
                continue
            }

            // --- Comment line: skip without changing state ---
            if (trimmed.startsWith("#")) {
                if (!commonSettings.KEEP_FIRST_COLUMN_COMMENT) {
                    val safeStatementIndent = if (statementIndent > 0) statementIndent else 0
                    lineProcessors.add(ContinuationIndentFixer(lineStart + safeStatementIndent, lineStart + leadingWs))
                }
                continue
            }

            // --- Top-level definition (no indentation): resets ---
            if (leadingWs == 0 && !metadataSection) {
                insideWrappableStatement = false
                currentMetadata = null
                identifiedArgumentsCount = 0
                if (matchesNextMetadataForLine(trimmed, statementMetadataCopy)) {
                    metadataSection = true
                } else {
                    continue
                }
            }

            if (currentMetadata == null) { // --- Check if this line matches a known statement from metadata ---
                val nextStatementMetadata = findMetadataForLine(trimmed, statementMetadataCopy, lineIdx)
                if (nextStatementMetadata != null) { // This line is a known statement start
                    insideWrappableStatement = nextStatementMetadata.wrappableArgumentCount > 0
                    statementIndent = leadingWs
                    currentMetadata = nextStatementMetadata
                    identifiedArgumentsCount = computeArgumentCountOnSameLineAsStatement(trimmed, currentMetadata)
                    continue
                }
            }

            // --- Existing continuation marker: valid, keep tracking ---
            if (trimmed.startsWith(GlobalConstants.CONTINUATION)) {
                if (!insideWrappableStatement) {
                    insideWrappableStatement = true
                } // Check for arguments/parameters within this continuation line - excluding any commentary. Arguments have to be separated by at least a SUPER SPACE
                val continuationFreeLineText = trimmed.substring(GlobalConstants.CONTINUATION.length).trimStart()
                identifiedArgumentsCount += computeArgumentCountOnLine(continuationFreeLineText, currentMetadata)

                val safeStatementIndent =
                    if (statementIndent > 0) statementIndent else 0 // Add a continuation indent fixer here to ensure correct indentation even for variable statements based on the statementIndent
                lineProcessors.add(ContinuationIndentFixer(lineStart + safeStatementIndent, lineStart + leadingWs))
                continue
            }

            // Check argument count limit: if we have metadata and have already emitted
            // enough continuation lines, this must be a new statement.
            if (currentMetadata != null && identifiedArgumentsCount >= currentMetadata.wrappableArgumentCount) { // We've exhausted the expected arguments → this is a new statement.
                currentMetadata = findMetadataForLine(trimmed, statementMetadataCopy, lineIdx)
                insideWrappableStatement = currentMetadata != null && currentMetadata.wrappableArgumentCount > 0
                statementIndent = leadingWs
                identifiedArgumentsCount = computeArgumentCountOnSameLineAsStatement(trimmed, currentMetadata)
                continue
            }

            // We ARE inside a wrappable statement. This line needs a continuation marker.
            if (leadingWs >= statementIndent) {
                lineProcessors.add(ContinuationInserter(lineStart + statementIndent))
                identifiedArgumentsCount += computeArgumentCountOnLine(trimmed, currentMetadata, identifiedArgumentsCount)
            } else {
                insideWrappableStatement = true
                statementIndent = leadingWs
                currentMetadata = findMetadataForLine(trimmed, statementMetadataCopy, lineIdx)
                identifiedArgumentsCount = 0
            }
        }

        return lineProcessors
    }

    private fun isMetadataSection(sectionName: String): Boolean = sectionName.equals(RobotNames.SETTING_SECTION_NAME, ignoreCase = true) || sectionName.equals(RobotNames.SETTINGS_SECTION_NAME,
        ignoreCase = true) || sectionName.equals(RobotNames.VARIABLE_SECTION_NAME, ignoreCase = true) || sectionName.equals(RobotNames.VARIABLES_SECTION_NAME, ignoreCase = true)

    /**
     * Attempts to find a [StatementMetadata] whose [StatementMetadata.identificationText]
     * appears at the beginning of [lineContent]. This is used as a fallback when the
     * line number based lookup doesn't match (e.g. because the formatter shifted lines).
     */
    private fun findMetadataForLine(lineContent: String, metadata: MutableList<StatementMetadata>, lineIdx: Int): StatementMetadata? {
        val nextMetadata = metadata.removeFirst()
        if (nextMetadata.normalizedIdentificationText.isEmpty() || !lineContent.replace(WHITESPACE_REGEX, "").startsWith(nextMetadata.normalizedIdentificationText))
            throw IllegalStateException("Collected metadata doesn't match with the current line. Expected metadata entry $nextMetadata for line content $lineContent at line $lineIdx")
        return nextMetadata
    }

    private fun matchesNextMetadataForLine(lineContent: String, metadata: MutableList<StatementMetadata>): Boolean {
        val nextMetadata = metadata.firstOrNull() ?: return false
        return nextMetadata.normalizedIdentificationText.isNotEmpty() && lineContent.replace(WHITESPACE_REGEX, "").startsWith(nextMetadata.normalizedIdentificationText)
    }

    private fun computeArgumentCountOnSameLineAsStatement(lineContent: String, metadata: StatementMetadata?): Int {
        if (metadata == null) return 0

        val normalizedLineContent = lineContent.replace(WHITESPACE_REGEX, "")
        val normalizedLineContentWithoutStatement = normalizedLineContent.substring(metadata.normalizedIdentificationText.length)
        return if (normalizedLineContentWithoutStatement.isEmpty() || normalizedLineContentWithoutStatement.startsWith('#')) 0
        else computeArgumentCountOnLine(normalizedLineContentWithoutStatement, metadata)
    }

    private fun computeArgumentCountOnLine(lineContent: String, metadata: StatementMetadata?, argumentStartIndex: Int = 0): Int {
        if (metadata == null) return 0

        val normalizedLineContent = lineContent.replace(WHITESPACE_REGEX, "")
        var argumentCount = 0
        var workableLineContent = normalizedLineContent
        for ((index, argument) in metadata.wrappableArguments.withIndex()) {
            if (argumentStartIndex > index) continue

            val normalizedArgument = argument.replace(WHITESPACE_REGEX, "")
            if (workableLineContent.startsWith(normalizedArgument)) {
                argumentCount++
                workableLineContent = workableLineContent.substring(normalizedArgument.length)
            } else break
        }
        return argumentCount
    }

    interface LineProcessor : Comparable<LineProcessor> {

        companion object {
            const val TAB_CHARACTER_SPACE_SIZE = 4
        }

        val offset: Int

        fun processLine(document: Document, commonSettings: CommonCodeStyleSettings, customSettings: RobotCodeStyleSettings): Int

        fun createInsertableSpace(spaceCount: Int, commonSettings: CommonCodeStyleSettings): String {
            val useTabCharacter = commonSettings.indentOptions?.USE_TAB_CHARACTER ?: false
            return if (useTabCharacter) {
                val tabCharacterCount = spaceCount / TAB_CHARACTER_SPACE_SIZE
                val spaceCharacterCount = spaceCount % TAB_CHARACTER_SPACE_SIZE
                "${"\t".repeat(tabCharacterCount)}${" ".repeat(spaceCharacterCount)}"
            } else " ".repeat(spaceCount)
        }

        override fun compareTo(other: LineProcessor): Int = offset.compareTo(other.offset)
    }

    class ContinuationInserter(override val offset: Int) : LineProcessor {

        override fun processLine(document: Document, commonSettings: CommonCodeStyleSettings, customSettings: RobotCodeStyleSettings): Int {
            val continuationIndentation = commonSettings.indentOptions?.CONTINUATION_INDENT_SIZE ?: 0
            val separatorSize = customSettings.AFTER_CONTINUATION_INDENT_SIZE

            val replaceableTextRange = TextRange(offset, offset + continuationIndentation + GlobalConstants.CONTINUATION.length + separatorSize)
            val textToReplace = document.getText(replaceableTextRange)
            val separator = createInsertableSpace(separatorSize, commonSettings)
            if (textToReplace.isBlank() || textToReplace.trimStart().length <= RobotCodeStyleSettings.SUPER_SPACE_SIZE) {/*
                 * Calculating the whitespace length before the first real character occurrence in this line (starting from the previously calculated insertion point). The end goal of this approach
                 * is removing any unnecessary whitespaces added by the formatter due to block structure and missing CONTINUATION tokens.
                 * It is usually relevant for variable assignments based on keywords, so something like
                 *
                 * ${Variable}=  My keyword  arg1   arg2
                 *
                 * That would me formatted to
                 *
                 * ${Variable}=  My keyword
                 * ...           arg1
                 * ...           arg2
                 *
                 * rather than
                 *
                 * ${Variable}=  My keyword
                 * ...    arg1
                 * ...    arg2
                 *
                 * because the CONTINUATION markers are added by this processor AFTER the formatting with the formatting rules are executed and the arguments are part of the keyword call which is part
                 * of the variable definition. Wrapping and alignment are handled relative to the parent's positioning. Thus, it needs to be taken care of here by us.
                 */
                val lineNumber = document.getLineNumber(offset)
                val lineEndOffset = document.getLineEndOffset(lineNumber)
                val lineTextRange = TextRange(offset, lineEndOffset)
                val lineText = document.getText(lineTextRange)
                val whitespaceLength = lineTextRange.length - lineText.trimStart().length

                val startOffset = offset + continuationIndentation
                document.replaceString(startOffset, offset + whitespaceLength, GlobalConstants.CONTINUATION + separator)
                return 0
            } // Not enough whitespaces available to put us into without changing too much (adding more whitespaces). Therefore, simply insert the required string with every whitespace requested.
            val continuationIndent = createInsertableSpace(continuationIndentation, commonSettings)
            document.insertString(offset, continuationIndent + GlobalConstants.CONTINUATION + separator)
            return continuationIndentation + GlobalConstants.CONTINUATION.length + separator.length
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            val that = other as ContinuationInserter
            return offset == that.offset
        }

        override fun hashCode(): Int {
            return offset
        }
    }

    class ContinuationIndentFixer(override val offset: Int, private val continuationOffset: Int) : LineProcessor {

        override fun processLine(document: Document, commonSettings: CommonCodeStyleSettings, customSettings: RobotCodeStyleSettings): Int {
            val continuationIndentation = commonSettings.indentOptions?.CONTINUATION_INDENT_SIZE ?: 0
            val diff = continuationOffset - offset
            return if (diff > continuationIndentation) {
                document.replaceString(offset, continuationOffset, createInsertableSpace(continuationIndentation, commonSettings))
                continuationIndentation - diff
            } else 0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            val that = other as ContinuationIndentFixer
            return offset == that.offset
        }

        override fun hashCode(): Int {
            return offset
        }
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
private val KEYWORD_CALL_ARGUMENT_NESTED_TYPES: Set<IElementType> = setOf(
    RobotTypes.KEYWORD_CALL,
    RobotTypes.POSITIONAL_ARGUMENT,
    RobotTypes.INLINE_ELSE_IF_STRUCTURE,
    RobotTypes.INLINE_ELSE_STRUCTURE
)

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

