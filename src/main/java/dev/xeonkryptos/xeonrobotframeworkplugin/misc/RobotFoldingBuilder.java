package dev.xeonkryptos.xeonrobotframeworkplugin.misc;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFoldable;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RobotFoldingBuilder extends CustomFoldingBuilder {

    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root, @NotNull Document document, boolean quick) {
        appendDescriptors(root, document, descriptors);
    }

    private void appendDescriptors(PsiElement element, Document document, List<FoldingDescriptor> descriptors) {
        if (element instanceof RobotFoldable foldable) {
            FoldingDescriptor foldingRegion = foldable.fold(document);
            if (foldingRegion != null) {
                descriptors.add(foldingRegion);
            }
        }

        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            appendDescriptors(child, document, descriptors);
        }
    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
        ItemPresentation presentation = ((NavigationItem) node.getPsi()).getPresentation();
        return presentation != null ? presentation.getPresentableText() : GlobalConstants.CONTINUATION;
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
