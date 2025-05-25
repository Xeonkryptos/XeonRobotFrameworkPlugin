package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Argument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinitionId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotStatementUpDownMover extends StatementUpDownMover {

    @Override
    public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo info, boolean down) {
        if (!(file instanceof RobotFile)) {
            return false;
        }

        LineRange range = getLineRangeFromSelection(editor);
        Pair<PsiElement, PsiElement> elementRange = getElementRange(editor, file, range);
        if (elementRange == null) {
            return false;
        }

        PsiElement startElement = elementRange.getFirst();
        PsiElement endElement = elementRange.getSecond();
        if (startElement instanceof PsiWhiteSpace || startElement instanceof LeafPsiElement) {
            startElement = endElement;
        }
        PsiElement block = findCompleteBlock(startElement);
        if (block == null) {
            return false;
        }

        Document document = editor.getDocument();
        LineRange blockRange = getBlockRange(block, document);
        if (blockRange == null) {
            return false;
        }

        PsiElement targetElement = findTargetElement(block, down);
        LineRange targetRange = getBlockRange(targetElement, document);
        if (targetRange == null) {
            return false;
        }

        info.toMove = blockRange;
        info.toMove2 = targetRange;

        return true;
    }

    @Nullable
    private PsiElement findCompleteBlock(@NotNull PsiElement element) {
        return switch (element) {
            case Argument argument -> argument;
            case KeywordStatement keywordStatement -> keywordStatement;
            case VariableDefinitionGroup variableDefinitionGroup -> variableDefinitionGroup;
            case KeywordDefinitionId keywordDefinitionId -> keywordDefinitionId.getParent();
            case KeywordDefinition keywordDefinition -> keywordDefinition;
            default -> null;
        };
    }

    @Nullable
    private LineRange getBlockRange(@NotNull PsiElement block, @NotNull Document document) {
        if (!block.isValid()) {
            return null;
        }

        TextRange textRange = block.getTextRange();
        int startLine = document.getLineNumber(textRange.getStartOffset());
        int endLine = document.getLineNumber(textRange.getEndOffset()) + 1;
        return new LineRange(startLine, endLine);
    }

    private PsiElement findTargetElement(@NotNull PsiElement block, boolean down) {
        PsiElement parent = block.getParent();
        if (down) {
            PsiElement next = block.getNextSibling();
            while (next != null && next.getParent() == parent) {
                if (isSameBlockType(next, block)) {
                    return next;
                }
                next = next.getNextSibling();
            }
        } else {
            PsiElement prev = block.getPrevSibling();
            while (prev != null && prev.getParent() == parent) {
                if (isSameBlockType(prev, block)) {
                    return prev;
                }
                prev = prev.getPrevSibling();
            }
        }

        return block;
    }

    private boolean isSameBlockType(@NotNull PsiElement element, @NotNull PsiElement reference) {
        if (element.getParent() != reference.getParent()) {
            return false;
        }
        if (element instanceof KeywordStatement || element instanceof VariableDefinitionGroup) {
            return reference instanceof KeywordStatement || reference instanceof VariableDefinitionGroup;
        }
        return element.getClass() == reference.getClass();
    }
}
