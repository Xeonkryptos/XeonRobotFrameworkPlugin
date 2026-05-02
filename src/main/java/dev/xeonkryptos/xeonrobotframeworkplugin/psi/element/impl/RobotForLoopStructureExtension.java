package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopHeader;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class RobotForLoopStructureExtension extends RobotExecutableStatementImpl implements RobotForLoopStructure {

    public RobotForLoopStructureExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull FoldingDescriptor @NotNull [] fold(@NotNull Document document, boolean quick) {
        RobotForLoopHeader forLoopHeader = getForLoopHeader();
        List<RobotExecutableStatement> executableStatements = getExecutableStatementList();
        PsiElement endElement = findChildByType(RobotTypes.END);
        if (endElement == null) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }
        List<FoldingDescriptor> foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForBlockStructure(this,
                                                                                                                            forLoopHeader,
                                                                                                                            executableStatements,
                                                                                                                            document,
                                                                                                                            endElement.getTextRange().getStartOffset());
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : FoldingDescriptor.EMPTY_ARRAY;
    }
}
