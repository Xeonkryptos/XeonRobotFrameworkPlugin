package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.application.options.CodeStyle
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor
import com.intellij.psi.tree.IElementType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants

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
 * After collapsing, the AST is re-parsed and all statements are single-line, allowing the
 * formatter to apply wrapping decisions cleanly. The [RobotPostFormatProcessor] then
 * re-inserts continuation markers where the formatter introduced new line breaks.
 */
class RobotPreFormatProcessor : PreFormatProcessor {

    override fun process(element: ASTNode, range: TextRange): TextRange {
        val psi = element.psi ?: return range
        val file = psi.containingFile ?: return range
        if (file.language !== RobotLanguage.INSTANCE) return range

        val document = file.viewProvider.document ?: return range
        val psiDocumentManager = PsiDocumentManager.getInstance(file.project)

        val commonSettings = CodeStyle.getLanguageSettings(file, RobotLanguage.INSTANCE)
        if (!hasWrappingEnabled(commonSettings)) return range

        // Use the AST to find statement boundaries, then collect continuation sequences
        // only within each statement's text range. This prevents cross-statement collapsing.
        val sequences = collectContinuationSequencesFromAST(element, document, range)
        if (sequences.isEmpty()) return range

        var totalDelta = 0
        for (seq in sequences.sortedByDescending { it.startOffset }) {
            val replacement = GlobalConstants.SUPER_SPACE
            val originalLength = seq.endOffset - seq.startOffset
            document.replaceString(seq.startOffset, seq.endOffset, replacement)
            totalDelta += originalLength - replacement.length
        }

        psiDocumentManager.commitDocument(document)

        return if (totalDelta > 0) range.grown(-totalDelta) else range
    }

    /**
     * Traverses the AST to find all statements that contain continuation markers, then
     * collects collapsible continuation sequences scoped to each statement.
     *
     * This is the key improvement over pure text scanning: by using the AST to determine
     * statement boundaries, we guarantee that continuations from **different** statements
     * are never collapsed together. For example:
     * ```
     *     &{GB}=  Some Keyword
     *     ...    art=X
     *     ...    teilbetrag=20,70
     *     &{EB1}=  Some Keyword
     *     ...    art=Y
     * ```
     * The `...` on lines 2-3 belong to the `&{GB}=` statement, and the `...` on line 5
     * belongs to `&{EB1}=`. Without AST scoping, the text scanner might collapse across
     * the `&{EB1}=` boundary.
     *
     * Within each statement, the chain-based inline-comment check is applied: if any
     * continuation line contains an inline comment, the entire statement's continuations
     * are left intact.
     */
    private fun collectContinuationSequencesFromAST(root: ASTNode, document: Document, range: TextRange): List<ContinuationSequence> {
        val text = document.text
        val result = mutableListOf<ContinuationSequence>()

        // Find all statement nodes whose text range overlaps with the formatting range
        val statementRanges = mutableListOf<TextRange>()
        collectStatementTextRanges(root, range, statementRanges)

        for (stmtRange in statementRanges) {
            // Scan for "..." only within this statement's text range
            val seqsForStatement = collectContinuationSequencesInRange(text, stmtRange, range)
            if (seqsForStatement.isEmpty()) continue

            // Chain-based comment check: if ANY continuation in this statement has an
            // inline comment, skip the entire statement's continuations
            val hasComment = seqsForStatement.any { it.hasInlineComment }
            if (!hasComment) {
                result.addAll(seqsForStatement)
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
        if (elementType in COLLAPSIBLE_STATEMENT_TYPES) {
            val nodeRange = node.textRange
            if (nodeRange.intersects(range)) {
                result.add(nodeRange)
            }
            // Don't recurse into children of matching statements — continuations within
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
    private fun collectContinuationSequencesInRange(
        text: String, statementRange: TextRange, formattingRange: TextRange
    ): List<ContinuationSequence> {
        val sequences = mutableListOf<ContinuationSequence>()
        val scanEnd = minOf(statementRange.endOffset, formattingRange.endOffset, text.length)

        var searchFrom = statementRange.startOffset
        while (searchFrom < scanEnd) {
            val dotsIdx = text.indexOf(GlobalConstants.CONTINUATION, searchFrom)
            if (dotsIdx < 0 || dotsIdx >= scanEnd) break

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
            val isValid = charAfterDots == '\t' || charAfterDots == '\n' || charAfterDots == '\r' ||
                    (charAfterDots == ' ' && afterDots + 1 < text.length && text[afterDots + 1] == ' ')
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

    private fun hasWrappingEnabled(commonSettings: CommonCodeStyleSettings): Boolean =
        commonSettings.CALL_PARAMETERS_WRAP != CommonCodeStyleSettings.DO_NOT_WRAP ||
                commonSettings.METHOD_PARAMETERS_WRAP != CommonCodeStyleSettings.DO_NOT_WRAP

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

    override fun processElement(source: PsiElement, settings: CodeStyleSettings): PsiElement = source

    override fun processText(source: PsiFile, rangeToReformat: TextRange, settings: CodeStyleSettings): TextRange {
        if (source.language !== RobotLanguage.INSTANCE) return rangeToReformat

        val commonSettings = settings.getCommonSettings(RobotLanguage.INSTANCE)
        if (!hasWrappingEnabled(commonSettings)) return rangeToReformat

        val customSettings = settings.getCustomSettings(RobotCodeStyleSettings::class.java)
        val document = source.viewProvider.document ?: return rangeToReformat

        val psiDocumentManager = PsiDocumentManager.getInstance(source.project)
        psiDocumentManager.commitDocument(document)

        val continuationIndentation = commonSettings.indentOptions?.CONTINUATION_INDENT_SIZE ?: 0
        val separatorSize = maxOf(customSettings.AFTER_CONTINUATION_INDENT_SIZE, RobotCodeStyleSettings.SUPER_SPACE_SIZE)

        // Remove dangling continuation markers ("..." lines with no arguments) that were
        // left behind after the PreFormatProcessor preserved a chain (e.g. due to inline
        // comments) and the formatter then wrapped arguments onto new lines.
        val removedChars = removeDanglingContinuationMarkers(document, rangeToReformat)
        val range = if (removedChars > 0) {
            psiDocumentManager.commitDocument(document)
            TextRange(rangeToReformat.startOffset, (rangeToReformat.endOffset - removedChars).coerceAtLeast(rangeToReformat.startOffset))
        } else {
            rangeToReformat
        }

        val insertions = collectContinuationInsertions(document, range, source)
        if (insertions.isEmpty()) return range

        // Insert in reverse document order to keep earlier offsets stable
        var totalDelta = 0
        for (insertion in insertions.sortedDescending()) {
            val replaceableTextRange = TextRange(insertion, insertion + continuationIndentation + GlobalConstants.CONTINUATION.length + separatorSize)
            val textToReplace = document.getText(replaceableTextRange)
            if (textToReplace.isBlank() || textToReplace.trimStart().length <= RobotCodeStyleSettings.SUPER_SPACE_SIZE) {
                val startOffset = insertion + continuationIndentation
                val recalculatedSeparator = " ".repeat(maxOf(separatorSize - textToReplace.length, RobotCodeStyleSettings.SUPER_SPACE_SIZE))
                document.replaceString(startOffset, startOffset + GlobalConstants.CONTINUATION.length + recalculatedSeparator.length, GlobalConstants.CONTINUATION + recalculatedSeparator)
            } else {
                val continuationIndent = " ".repeat(continuationIndentation)
                val separator = " ".repeat(separatorSize)
                document.insertString(insertion, continuationIndent + GlobalConstants.CONTINUATION + separator)
                totalDelta += continuationIndentation + GlobalConstants.CONTINUATION.length + separator.length
            }
        }

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
     *     ...
     * ```
     * Such a line is invalid Robot Framework syntax (a continuation marker must be
     * followed by content on the same line or have no continuation at all).
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

            if (lineStart > 0) {
                // Delete the newline before this line + the entire line content
                // This effectively removes the line without leaving a blank line
                deleteFrom = lineStart - 1 // include the \n (or last char of \r\n)
                deleteTo = lineEnd
                // Handle \r\n: if the character before \n is \r, include it too
                val adjustedFrom = if (deleteFrom > 0 && text[deleteFrom] == '\n' && text[deleteFrom - 1] == '\r') {
                    deleteFrom - 1
                } else {
                    deleteFrom
                }
                document.deleteString(adjustedFrom, deleteTo)
                totalRemoved += deleteTo - adjustedFrom
            } else {
                // First line in document — delete the line content + trailing newline
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
     * **Core insight:** The formatter's wrapping mechanism can only create new lines within
     * existing wrappable statements (KEYWORD_CALL and LOCAL_ARGUMENTS_SETTING). The
     * [RobotPreFormatProcessor] has already collapsed collapsible continuations into single
     * lines before the formatter ran. Therefore, after the formatter has wrapped, every
     * indented line that is NOT one of the following must be a wrapped continuation:
     * - A statement start (identified via PSI analysis)
     * - A structural keyword (FOR, IF, END, \[Tags], etc.)
     * - A comment line (starting with #)
     * - An empty/blank line
     * - A top-level definition (no indentation)
     * - An existing continuation marker line (starting with "...")
     *
     * This approach does NOT rely on textual heuristics like super-space detection.
     * It relies solely on the PSI tree for statement-start detection and on the Robot
     * Framework syntax rules for everything else.
     *
     * @param psiFile The PSI file, used for PSI-based statement-start detection
     */
    private fun collectContinuationInsertions(document: Document, range: TextRange, psiFile: PsiFile): List<Int> {
        val text = document.text
        if (text.isEmpty()) return emptyList()

        val insertions = mutableListOf<Int>()

        val safeEndOffset = minOf(range.endOffset, text.length).coerceAtLeast(0)
        if (range.startOffset >= safeEndOffset) return emptyList()

        val startLine = document.getLineNumber(range.startOffset)
        val endLine = document.getLineNumber((safeEndOffset - 1).coerceAtLeast(0))

        // Collect statement start lines from the PSI tree. Even though the AST may be
        // partially broken after wrapping, the start positions of statement nodes are
        // still reliable because wrapping only inserts whitespace — it does not move
        // the first token of a statement.
        val statementInfo = collectStatementStartLines(psiFile, document, range)

        // Track whether the current context is a wrappable statement.
        // We enter a wrappable context when we see a wrappable statement start or an
        // existing continuation marker. We leave it on empty lines, section headers,
        // top-level definitions, or non-wrappable statement starts.
        var insideWrappableStatement = false
        var statementIndent = -1

        for (lineIdx in startLine..endLine) {
            val lineStart = document.getLineStartOffset(lineIdx)
            val lineEnd = document.getLineEndOffset(lineIdx)
            val lineText = text.substring(lineStart, lineEnd)
            val trimmed = lineText.trimStart()
            val leadingWs = lineText.length - trimmed.length

            // --- Empty line: breaks any ongoing statement ---
            if (trimmed.isEmpty()) {
                insideWrappableStatement = false
                continue
            }

            // --- Section header (e.g. "*** Keywords ***"): always resets ---
            if (trimmed.startsWith("*")) {
                insideWrappableStatement = false
                continue
            }

            // --- Comment line: skip without changing state ---
            // A comment line does not break a wrappable statement context because
            // comments can appear between continuation lines in Robot Framework.
            if (trimmed.startsWith("#")) {
                continue
            }

            // --- Top-level definition (no indentation): resets ---
            if (leadingWs == 0) {
                insideWrappableStatement = false
                continue
            }

            // --- Existing continuation marker: valid, keep tracking ---
            if (trimmed.startsWith(GlobalConstants.CONTINUATION)) {
                // This line already has "..." — it's a preserved continuation from the
                // PreFormatProcessor (e.g. because the chain contained a comment).
                // Stay in wrappable context.
                if (!insideWrappableStatement) {
                    insideWrappableStatement = true
                    statementIndent = leadingWs
                }
                continue
            }

            // --- Structural keyword or setting (FOR, IF, [Tags], etc.): new statement ---
            if (isStructuralKeywordLine(trimmed)) {
                if (trimmed.matches(Regex("^\\[\\s*Arguments\\s*].*", RegexOption.IGNORE_CASE))) {
                    insideWrappableStatement = true
                    statementIndent = leadingWs
                } else {
                    insideWrappableStatement = false
                }
                continue
            }

            // --- PSI-identified statement start ---
            if (lineIdx in statementInfo.allStatementStartLines) {
                insideWrappableStatement = lineIdx in statementInfo.wrappableStatementStartLines
                if (insideWrappableStatement) {
                    statementIndent = leadingWs
                }
                continue
            }

            // --- At this point: indented, non-structural, non-comment, no "..." ---
            // This line was NOT identified as a statement start by PSI analysis.
            //
            // Since the formatter only creates new lines through wrapping within
            // wrappable statements, and the PreFormatProcessor already collapsed
            // all collapsible continuations, this line MUST be one of:
            // (a) A wrapped argument/parameter from a wrappable statement → needs "..."
            // (b) A line that the broken AST failed to identify as a statement start
            //
            // Case (b) is rare and only happens when the AST is severely broken.
            // To handle it safely, we only insert "..." if we are currently tracking
            // a wrappable statement context. If we're not in a wrappable context,
            // we conservatively start tracking (it could be an unrecognized keyword call).

            if (!insideWrappableStatement) {
                // Not in a wrappable context — this is likely an unrecognized statement
                // start (e.g. a keyword call that the broken AST didn't parse).
                // Start tracking it as a potential wrappable statement.
                insideWrappableStatement = true
                statementIndent = leadingWs
                continue
            }

            // We ARE inside a wrappable statement. This line needs a continuation marker.
            if (leadingWs >= statementIndent) {
                insertions.add(lineStart + statementIndent)
            } else {
                // Indentation decreased — we've left the statement scope.
                // This line is likely a new statement that the AST didn't recognize.
                insideWrappableStatement = true
                statementIndent = leadingWs
            }
        }

        return insertions
    }

    /**
     * Traverses the PSI tree and collects line numbers where statements start.
     *
     * Returns a [StatementStartInfo] containing:
     * - [StatementStartInfo.allStatementStartLines]: all statement start lines (any type)
     * - [StatementStartInfo.wrappableStatementStartLines]: only KEYWORD_CALL and
     *   LOCAL_ARGUMENTS_SETTING start lines
     *
     * This information is used by the line scanner to distinguish statement-start lines
     * from continuation lines even when the AST is partially broken after wrapping.
     */
    private fun collectStatementStartLines(psiFile: PsiFile, document: Document, range: TextRange): StatementStartInfo {
        val allLines = mutableSetOf<Int>()
        val wrappableLines = mutableSetOf<Int>()
        collectStartLinesRecursive(psiFile.node, document, range, allLines, wrappableLines)
        return StatementStartInfo(allLines, wrappableLines)
    }

    private fun collectStartLinesRecursive(
        node: ASTNode, document: Document, range: TextRange, allResult: MutableSet<Int>, wrappableResult: MutableSet<Int>
    ) {
        val elementType = node.elementType
        val isStatement = elementType in STATEMENT_TYPES
        val isWrappable = elementType === RobotTypes.KEYWORD_CALL || elementType === RobotTypes.LOCAL_ARGUMENTS_SETTING

        if (isStatement || isWrappable) {
            val startOffset = node.startOffset
            if (startOffset >= range.startOffset && startOffset < range.endOffset) {
                val line = document.getLineNumber(startOffset)
                if (isStatement) allResult.add(line)
                if (isWrappable) {
                    allResult.add(line)
                    wrappableResult.add(line)
                }
            }
        }
        var child = node.firstChildNode
        while (child != null) {
            collectStartLinesRecursive(child, document, range, allResult, wrappableResult)
            child = child.treeNext
        }
    }

    private data class StatementStartInfo(val allStatementStartLines: Set<Int>, val wrappableStatementStartLines: Set<Int>)

    /**
     * Determines whether [trimmed] (a line with leading whitespace already stripped) starts
     * with a Robot Framework structural keyword that indicates a new statement rather than
     * a continuation of the previous one.
     *
     * Structural keywords include:
     * - Local settings: `\[Arguments]`, `\[Return]`, `\[Documentation]`, `\[Tags]`, `\[Setup]`,
     *   `\[Teardown]`, `\[Timeout]`, `\[Template]`
     * - Control flow: FOR, WHILE, IF, ELSE IF, ELSE, TRY, EXCEPT, FINALLY, END
     * - Other: BREAK, CONTINUE, RETURN, VAR, GROUP
     */
    private fun isStructuralKeywordLine(trimmed: String): Boolean {
        // Local settings always start with '['
        if (trimmed.startsWith("[")) return true

        // Check if the line starts with a known structural keyword followed by either
        // whitespace (indicating arguments) or end of line (keyword alone on a line).
        // We must be careful not to match keyword CALL names that happen to start with
        // a structural keyword prefix, e.g. "IF Connection Exists" is a keyword call,
        // not a structural IF. Robot Framework structural keywords must be followed by
        // at least 2 spaces (super-space separator) or end of line.
        for (keyword in STRUCTURAL_KEYWORDS) {
            if (trimmed.startsWith(keyword)) {
                val afterKeyword = trimmed.length - keyword.length
                if (afterKeyword == 0) return true // keyword alone on line
                val charAfter = trimmed[keyword.length]
                // In Robot Framework, structural keywords are separated from their arguments
                // by at least 2 spaces or a tab
                if (charAfter == '\t') return true
                if (charAfter == ' ' && afterKeyword >= 2 && trimmed[keyword.length + 1] == ' ') return true
            }
        }
        return false
    }


    private fun hasWrappingEnabled(commonSettings: CommonCodeStyleSettings): Boolean =
        commonSettings.CALL_PARAMETERS_WRAP != CommonCodeStyleSettings.DO_NOT_WRAP ||
                commonSettings.METHOD_PARAMETERS_WRAP != CommonCodeStyleSettings.DO_NOT_WRAP
}

/**
 * Set of Robot Framework keywords that start their own statement block and therefore
 * must NOT be treated as wrapped continuations of a preceding statement.
 */
private val STRUCTURAL_KEYWORDS = setOf(
    "FOR", "WHILE", "IF", "ELSE", "ELSE IF", "TRY", "EXCEPT", "FINALLY", "END",
    "BREAK", "CONTINUE", "RETURN", "VAR", "GROUP"
)

/**
 * Set of Robot Framework element types that represent standalone statements.
 * Any element with one of these types starts a new statement on its own line —
 * it is NOT a continuation of a preceding statement.
 *
 * This includes wrappable statement types (KEYWORD_CALL, LOCAL_ARGUMENTS_SETTING)
 * as well as non-wrappable ones (control flow structures, local settings, etc.).
 */
private val STATEMENT_TYPES: Set<IElementType> = setOf(
    // Wrappable statements
    RobotTypes.KEYWORD_CALL,
    RobotTypes.LOCAL_ARGUMENTS_SETTING,
    // Local settings (non-wrappable in current implementation)
    RobotTypes.LOCAL_SETTING,
    // Variable statements
    RobotTypes.KEYWORD_VARIABLE_STATEMENT,
    RobotTypes.SINGLE_VARIABLE_STATEMENT,
    RobotTypes.INLINE_VARIABLE_STATEMENT,
    RobotTypes.EMPTY_VARIABLE_STATEMENT,
    RobotTypes.IF_VARIABLE_STATEMENT,
    // Control flow structures
    RobotTypes.FOR_LOOP_STRUCTURE,
    RobotTypes.WHILE_LOOP_STRUCTURE,
    RobotTypes.CONDITIONAL_STRUCTURE,
    RobotTypes.EXCEPTION_HANDLING_STRUCTURE,
    RobotTypes.GROUP_STRUCTURE,
    RobotTypes.LOOP_CONTROL_STRUCTURE,
    RobotTypes.RETURN_STRUCTURE,
    // Executable wrapper
    RobotTypes.EXECUTABLE_STATEMENT
)

/**
 * Set of Robot Framework element types whose AST nodes may contain continuation markers
 * ("..."). The [RobotPreFormatProcessor] scopes its continuation scanning to within
 * each of these nodes, preventing cross-statement collapsing.
 *
 * This includes all statement types that can span multiple lines using "...":
 * - Keyword calls (with or without variable assignments)
 * - Local settings ([Arguments], [Documentation], etc.)
 * - Variable definitions in the *** Variables *** section
 */
private val COLLAPSIBLE_STATEMENT_TYPES: Set<IElementType> = setOf(
    RobotTypes.KEYWORD_CALL,
    RobotTypes.LOCAL_ARGUMENTS_SETTING,
    RobotTypes.LOCAL_SETTING,
    RobotTypes.KEYWORD_VARIABLE_STATEMENT,
    RobotTypes.SINGLE_VARIABLE_STATEMENT,
    RobotTypes.INLINE_VARIABLE_STATEMENT,
    RobotTypes.EMPTY_VARIABLE_STATEMENT,
    RobotTypes.IF_VARIABLE_STATEMENT,
    RobotTypes.EXECUTABLE_STATEMENT,
    // Control flow headers can also have continuations (e.g. FOR with many items)
    RobotTypes.FOR_LOOP_HEADER,
    RobotTypes.WHILE_LOOP_HEADER,
    RobotTypes.IF,
    RobotTypes.ELSE_IF,
    RobotTypes.EXCEPT,
    RobotTypes.GROUP_HEADER
)

