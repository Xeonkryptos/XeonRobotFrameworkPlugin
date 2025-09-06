package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RobotFoldingBuilder extends CustomFoldingBuilder {

    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root, @NotNull Document document, boolean quick) {
        appendDescriptors(root, document, descriptors);
    }

    private void appendDescriptors(PsiElement element, Document document, List<FoldingDescriptor> descriptors) {
        if (element instanceof RobotStatement) {
            TextRange textRange = element.getTextRange();
            textRange = computeOptimizedTextRange(element, textRange, document);
            if (textRange.getLength() > 0) {
                descriptors.add(new FoldingDescriptor(element, textRange));
            }
        }

        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            appendDescriptors(child, document, descriptors);
        }
    }

    private static TextRange computeOptimizedTextRange(PsiElement element, TextRange textRange, Document document) {
        int ignorableNewLines = 0;
        CharSequence charsSequence = document.getCharsSequence();
        for (int i = textRange.getEndOffset() - 1; i >= textRange.getStartOffset(); i--) {
            char c = charsSequence.charAt(i);
            if (c != '\n' && c != '\r') {
                int offset = textRange.getEndOffset() - ignorableNewLines;
                PsiElement elementAt = element.findElementAt(offset);
                if (elementAt != null && elementAt.getNode().getElementType() == RobotTypes.EOL) {
                    return computeTextRangeWithoutCommentsInDifferentLines(elementAt, textRange, document);
                }
                break;
            }
            // Count any new lines at the end of this element to ignore them in the folding region later on. That way, newlines which are a part of the element
            // tree structure as children are ignored, and the folding region keeps the basic spaces/structure of the code without too much interruption.
            ignorableNewLines++;
        }
        return textRange.grown(-ignorableNewLines);
    }

    private static TextRange computeTextRangeWithoutCommentsInDifferentLines(PsiElement elementAt, TextRange textRange, Document document) {
        PsiElement prevSibling = elementAt.getPrevSibling();
        ASTNode prevSiblingNode = prevSibling.getNode();
        while (prevSiblingNode.getElementType() == RobotTypes.EOL || prevSiblingNode.getElementType() == TokenType.WHITE_SPACE) {
            prevSibling = prevSibling.getPrevSibling();
            prevSiblingNode = prevSibling.getNode();
        }
        int newEndOffset = prevSibling.getTextRange().getEndOffset();
        // Look for the end offset of the current line identified as the relevant line to stop the folding region at. That way, any comment in the same line
        // as the last relevant element is also included in the folding region. Any other comments in different lines are excluded.
        int lineNumber = document.getLineNumber(newEndOffset);
        newEndOffset = document.getLineEndOffset(lineNumber);
        return new TextRange(textRange.getStartOffset(), newEndOffset);
    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
        ItemPresentation presentation;
        if ((presentation = ((NavigationItem) node.getPsi()).getPresentation()) != null) {
            return presentation.getPresentableText();
        } else {
            return GlobalConstants.ELLIPSIS;
        }
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
        return node.getElementType() == RobotTypes.SETTINGS_SECTION;
    }
}
