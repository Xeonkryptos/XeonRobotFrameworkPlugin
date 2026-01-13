package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class RobotConditionalStructureExtension extends RobotExecutableStatementImpl implements RobotConditionalStructure {

    public RobotConditionalStructureExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public FoldingDescriptor[] fold(@NotNull Document document) {
        List<FoldingDescriptor> foldingDescriptors = new ArrayList<>();
        List<RobotExecutableStatement> executableStatements = getExecutableStatementList();
        if (executableStatements.size() == 1) {
            // On single if-clauses, fold the if block up to the END statement. The END statement moves into the same line as the if statement.
            foldSingleIfClause(document, executableStatements, foldingDescriptors);
        } else {
            foldMultipleClauses(document, executableStatements, foldingDescriptors);
        }
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : null;
    }

    private void foldSingleIfClause(@NotNull Document document, List<RobotExecutableStatement> executableStatements, List<FoldingDescriptor> foldingDescriptors) {
        RobotExecutableStatement ifBlock = executableStatements.getFirst();

        PsiElement endElement = getLastChild();
        while (endElement != null && endElement.getNode().getElementType() != RobotTypes.END) {
            endElement = endElement.getPrevSibling();
        }

        List<RobotExecutableStatement> ifBlockExecutableStatements = ifBlock.getExecutableStatementList();
        RobotConditionalContent conditionalContent = PsiTreeUtil.findChildOfType(ifBlock, RobotConditionalContent.class, true, RobotExecutableStatement.class);
        if (endElement == null || conditionalContent == null) {
            List<FoldingDescriptor> conditionalBlockFoldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForBlockStructure(this, ifBlockExecutableStatements, document);
            foldingDescriptors.addAll(conditionalBlockFoldingDescriptors);
        } else {
            int endElementStartOffset = endElement.getTextRange().getStartOffset();
            List<FoldingDescriptor> conditionalFoldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForBlockStructure(getNode(),
                                                                                                                                           conditionalContent,
                                                                                                                                           endElementStartOffset,
                                                                                                                                           ifBlockExecutableStatements,
                                                                                                                                           document);
            foldingDescriptors.addAll(conditionalFoldingDescriptors);
        }
    }

    private static void foldMultipleClauses(@NotNull Document document, List<RobotExecutableStatement> executableStatements, List<FoldingDescriptor> foldingDescriptors) {
        for (RobotExecutableStatement conditionalBlock : executableStatements) {
            List<RobotExecutableStatement> conditionalBlockExecutableStatements = conditionalBlock.getExecutableStatementList();
            List<FoldingDescriptor> conditionalBlockFoldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForBlockStructure(conditionalBlock,
                                                                                                                                                conditionalBlockExecutableStatements,
                                                                                                                                                document);
            foldingDescriptors.addAll(conditionalBlockFoldingDescriptors);
        }
    }
}
