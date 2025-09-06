package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
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
            int ignorableNewLines = 0;
            CharSequence charsSequence = document.getCharsSequence();
            TextRange textRange = element.getTextRange();
            for (int i = textRange.getEndOffset() - 1; i >= textRange.getStartOffset(); i--) {
                char c = charsSequence.charAt(i);
                if (c != '\n' && c != '\r') {
                    break;
                }
                ignorableNewLines++;
            }
            textRange = textRange.grown(-ignorableNewLines);
            if (textRange.getLength() > 0) {
                descriptors.add(new FoldingDescriptor(element, textRange));
            }
        }

        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            appendDescriptors(child, document, descriptors);
        }
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
