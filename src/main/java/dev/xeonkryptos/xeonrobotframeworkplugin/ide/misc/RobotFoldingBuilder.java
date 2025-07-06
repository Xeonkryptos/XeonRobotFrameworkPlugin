package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

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
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RobotFoldingBuilder implements FoldingBuilder, DumbAware {

    private static final Set<IElementType> CONTROL_STRUCTURE_TOKENS = Set.of(RobotTypes.WHILE,
                                                                             RobotTypes.FOR,
                                                                             RobotTypes.FOR_IN,
                                                                             RobotTypes.VAR,
                                                                             RobotTypes.GIVEN,
                                                                             RobotTypes.WHEN,
                                                                             RobotTypes.THEN,
                                                                             RobotTypes.AND,
                                                                             RobotTypes.BUT,
                                                                             RobotTypes.IF,
                                                                             RobotTypes.ELSE_IF,
                                                                             RobotTypes.ELSE,
                                                                             RobotTypes.TRY,
                                                                             RobotTypes.EXCEPT,
                                                                             RobotTypes.FINALLY,
                                                                             RobotTypes.END,
                                                                             RobotTypes.RETURN,
                                                                             RobotTypes.GROUP,
                                                                             RobotTypes.BREAK,
                                                                             RobotTypes.CONTINUE);

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        appendDescriptors(node, descriptors);
        return processDescriptors(descriptors).toArray(FoldingDescriptor.EMPTY_ARRAY);
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
            if (!descriptor.getElement().getText().contains("\n") && !CONTROL_STRUCTURE_TOKENS.contains(descriptor.getElement().getElementType())) {
                descriptorsCopy.remove(descriptor);
            }
        }

        List<FoldingDescriptor> finalDescriptors = new ArrayList<>(descriptorsCopy);

        for (int i = 0; i < finalDescriptors.size(); i++) {
            ASTNode element = finalDescriptors.get(i).getElement();
            if (CONTROL_STRUCTURE_TOKENS.contains(element.getElementType()) && !element.getText().equals("END")) {
                int nextIndex = i + 1;
                if (nextIndex >= finalDescriptors.size()) {
                    break;
                }

                TextRange combinedRange = null;
                FoldingDescriptor endDescriptor = null;
                for (FoldingDescriptor descriptor : finalDescriptors.subList(nextIndex, finalDescriptors.size())) {
                    ASTNode nextElement = descriptor.getElement();
                    if (CONTROL_STRUCTURE_TOKENS.contains(nextElement.getElementType()) && nextElement.getText().equals("END")
                        && computeOffset(element) == computeOffset(nextElement)) {
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
            return CONTROL_STRUCTURE_TOKENS.contains(node.getElementType()) ? presentation.getPresentableText() + " ..." : presentation.getPresentableText();
        } else {
            return "...";
        }
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return node.getElementType() == RobotTypes.SETTINGS_SECTION;
    }
}
