package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.removeUserData
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames

val WHITESPACE_REGEX = Regex("\\s*")

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
        return if (source.isValid) source else psiFile.findElementAt(textRange.startOffset) ?: source
    }

    override fun processText(source: PsiFile, rangeToReformat: TextRange, settings: CodeStyleSettings): TextRange {
        if (source.language !== RobotLanguage.INSTANCE) return rangeToReformat

        // Read statement metadata collected by the PreFormatProcessor. Without it, correct insertion locations can't be identified
        val statementMetadata = source.removeUserData(STATEMENT_METADATA_KEY) ?: return rangeToReformat

        val commonSettings = settings.getCommonSettings(RobotLanguage.INSTANCE)
        val customSettings = settings.getCustomSettings(RobotCodeStyleSettings::class.java)

        val document = source.fileDocument
        // Remove dangling continuation markers ("..." lines with no arguments) that were
        // left behind after the PreFormatProcessor preserved a chain (e.g. due to inline
        // comments) and the formatter then wrapped arguments onto new lines.
        val removedChars = removeDanglingContinuationMarkers(document, rangeToReformat)
        val range = if (removedChars > 0) TextRange(rangeToReformat.startOffset, (rangeToReformat.endOffset - removedChars).coerceAtLeast(rangeToReformat.startOffset))
        else rangeToReformat

        val lineProcessors = collectContinuationInsertions(document, range, commonSettings, statementMetadata)
        if (lineProcessors.isEmpty()) return range

        // Insert in reverse document order to keep earlier offsets stable
        val totalDelta = lineProcessors.sortedDescending().sumOf { it.processLine(document, commonSettings, customSettings) }

        PsiDocumentManager.getInstance(source.project).commitDocument(document)

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

        val startLine = document.getLineNumber(0)
        val endLine = document.lineCount - 1

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
            val inFormattingRange = range.intersects(lineStart, lineEnd)

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
                    if (inFormattingRange) lineProcessors.add(ContinuationIndentFixer(lineStart + safeStatementIndent, lineStart + leadingWs))
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
                if (inFormattingRange) lineProcessors.add(ContinuationIndentFixer(lineStart + safeStatementIndent, lineStart + leadingWs))
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
                if (inFormattingRange) lineProcessors.add(ContinuationInserter(lineStart + statementIndent))
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
        if (nextMetadata.normalizedIdentificationText.isEmpty() || !lineContent.replace(WHITESPACE_REGEX, "").startsWith(nextMetadata.normalizedIdentificationText)) {
            throw IllegalStateException("Collected metadata doesn't match with the current line. Expected metadata entry $nextMetadata for line content $lineContent at line $lineIdx")
        }
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

        override fun hashCode(): Int = offset
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

        override fun hashCode(): Int = offset
    }
}
