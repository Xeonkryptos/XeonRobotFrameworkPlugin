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
import com.intellij.psi.util.startOffset
import com.intellij.util.DocumentUtil
import com.jetbrains.python.ast.impl.PyPsiUtilsCore
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
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
 */
class RobotPreFormatProcessor : PreFormatProcessor {

    override fun process(element: ASTNode, range: TextRange): TextRange {
        val psi = element.psi ?: return range
        if (psi.language !== RobotLanguage.INSTANCE) return range
        @Suppress("UnstableApiUsage") PyPsiUtilsCore.assertValid(psi)
        val file = psi.containingFile ?: return range

        var currentRange = range
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
