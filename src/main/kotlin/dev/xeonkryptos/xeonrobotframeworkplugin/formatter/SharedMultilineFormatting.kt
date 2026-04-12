package dev.xeonkryptos.xeonrobotframeworkplugin.formatter

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

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
data class StatementMetadata(val identificationText: String, val wrappableArguments: List<String>, val initialTextRange: TextRange) {
    val wrappableArgumentCount: Int = wrappableArguments.size
    val normalizedIdentificationText: String = identificationText.replace(WHITESPACE_REGEX, "").trim()
}

/**
 * [Key] used to attach a list of [StatementMetadata] to the [PsiFile] as user data.
 * Written by [RobotPreFormatProcessor], read by [RobotPostFormatProcessor].
 */
val STATEMENT_METADATA_KEY: Key<List<StatementMetadata>> = Key.create("ROBOT_STATEMENT_METADATA")
