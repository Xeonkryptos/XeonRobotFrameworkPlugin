package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptHeader;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class RobotExceptStructureExtension extends RobotExecutableStatementImpl implements RobotExceptStructure {
    public RobotExceptStructureExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull FoldingDescriptor @NotNull [] fold(@NotNull Document document, boolean quick) {
        RobotExceptHeader exceptHeader = getExceptHeader();
        List<RobotExecutableStatement> executableStatements = getExecutableStatementList();
        List<FoldingDescriptor> foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForBlockStructure(this, exceptHeader, executableStatements, document);
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : FoldingDescriptor.EMPTY_ARRAY;
    }
}
