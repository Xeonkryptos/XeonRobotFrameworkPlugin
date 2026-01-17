package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTryStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RobotTryStructureExtension extends RobotExecutableStatementImpl implements RobotTryStructure {
    public RobotTryStructureExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable FoldingDescriptor[] fold(@NotNull Document document) {
        PsiElement firstChild = getFirstChild(); // TRY token
        List<RobotExecutableStatement> executableStatements = getExecutableStatementList();
        List<FoldingDescriptor> foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForBlockStructure(this, firstChild, executableStatements, document);
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : null;
    }
}
