package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Heading;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RobotFoldingBuilder implements FoldingBuilder, DumbAware {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        try {
            this.appendDescriptors(node, descriptors);
            return this.processDescriptors(descriptors).toArray(new FoldingDescriptor[0]);
        } catch (Throwable e) {
            return descriptors.toArray(new FoldingDescriptor[0]);
        }
    }

    private static int computeOffset(ASTNode node) {
        Project project = node.getPsi().getProject();
        PsiFile containingFile = node.getPsi().getContainingFile();
        Document document = PsiDocumentManager.getInstance(project).getDocument(containingFile);
        if (document != null) {
            int lineNumber = document.getLineNumber(node.getTextRange().getStartOffset());
            return node.getTextRange().getStartOffset() - document.getLineStartOffset(lineNumber);
        } else {
            return -1;
        }
    }

    private Collection<FoldingDescriptor> processDescriptors(Collection<FoldingDescriptor> descriptors) {
        List<FoldingDescriptor> descriptorsCopy = new ArrayList<>(descriptors);
        int count = 0;
        LinkedList<LinkedList<FoldingDescriptor>> groupedComments = new LinkedList<>();

        for (FoldingDescriptor descriptor : descriptors) {
            if (descriptor.getElement().getPsi() instanceof PsiComment) {
                if (count > 0) {
                    groupedComments.getLast().add(descriptor);
                } else {
                    count = 1;
                    LinkedList<FoldingDescriptor> newGroup = new LinkedList<>();
                    newGroup.add(descriptor);
                    groupedComments.add(newGroup);
                }
                descriptorsCopy.remove(descriptor);
            } else {
                count = 0;
            }
        }

        for (LinkedList<FoldingDescriptor> group : groupedComments) {
            if (group.size() > 1) {
                TextRange combinedRange = null;
                for (FoldingDescriptor descriptor : group) {
                    combinedRange = combinedRange == null ? descriptor.getRange() : combinedRange.union(descriptor.getRange());
                }
                descriptorsCopy.add(new FoldingDescriptor(group.getFirst().getElement(), combinedRange));
            }
        }

        for (FoldingDescriptor descriptor : descriptors) {
            if (!descriptor.getElement().getText().contains("\n") && !descriptor.getElement().getElementType().equals(RobotTokenTypes.SYNTAX_MARKER)) {
                descriptorsCopy.remove(descriptor);
            }
        }

        List<FoldingDescriptor> finalDescriptors = new ArrayList<>(descriptorsCopy);

        for (int i = 0; i < finalDescriptors.size(); i++) {
            ASTNode element = finalDescriptors.get(i).getElement();
            if (element.getElementType().equals(RobotTokenTypes.SYNTAX_MARKER) && !element.getText().equals("END")) {
                int nextIndex = i + 1;
                if (nextIndex >= finalDescriptors.size()) {
                    break;
                }

                TextRange combinedRange = null;
                FoldingDescriptor endDescriptor = null;
                for (FoldingDescriptor descriptor : finalDescriptors.subList(nextIndex, finalDescriptors.size())) {
                    ASTNode nextElement = descriptor.getElement();
                    if (nextElement.getElementType().equals(RobotTokenTypes.SYNTAX_MARKER) && nextElement.getText().equals("END") && computeOffset(element) == computeOffset(nextElement)) {
                        endDescriptor = descriptor;
                        combinedRange = element.getTextRange().union(nextElement.getTextRange());
                        break;
                    }
                }

                finalDescriptors.remove(i);
                if (endDescriptor != null) {
                    finalDescriptors.remove(endDescriptor);
                }

                if (combinedRange != null) {
                    finalDescriptors.add(new FoldingDescriptor(element, combinedRange));
                }
            }
        }

        descriptors.clear();
        return finalDescriptors;
    }

    private void appendDescriptors(ASTNode node, Collection<FoldingDescriptor> descriptors) {
        if (node.getPsi() instanceof RobotStatement || node.getPsi() instanceof PsiComment) {
            try {
                descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
            } catch (Throwable ignored) {
            }
        }

        for (ASTNode childNode = node.getFirstChildNode(); childNode != null; childNode = childNode.getTreeNext()) {
            this.appendDescriptors(childNode, descriptors);
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        ItemPresentation presentation;
        if ((presentation = ((NavigationItem) node.getPsi()).getPresentation()) != null) {
            return RobotTokenTypes.SYNTAX_MARKER.equals(node.getElementType()) ? presentation.getPresentableText() + " ..." : presentation.getPresentableText();
        } else {
            return "...";
        }
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return node.getPsi() instanceof Heading && ((Heading) node.getPsi()).isSettings();
    }
}
