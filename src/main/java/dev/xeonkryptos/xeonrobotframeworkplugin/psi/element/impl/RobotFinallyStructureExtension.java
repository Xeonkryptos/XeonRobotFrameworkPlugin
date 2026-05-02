package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFinallyStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class RobotFinallyStructureExtension extends RobotExecutableStatementImpl implements RobotFinallyStructure {
    public RobotFinallyStructureExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull FoldingDescriptor @NotNull [] fold(@NotNull Document document, boolean quick) {
        PsiElement firstChild = getFirstChild(); // FINALLY token
        List<RobotExecutableStatement> executableStatements = getExecutableStatementList();
        List<FoldingDescriptor> foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForBlockStructure(this, firstChild, executableStatements, document);
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : FoldingDescriptor.EMPTY_ARRAY;
    }
}
