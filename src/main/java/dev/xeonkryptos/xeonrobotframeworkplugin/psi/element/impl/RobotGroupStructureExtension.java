package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGroupHeader;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGroupStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RobotGroupStructureExtension extends RobotExecutableStatementImpl implements RobotGroupStructure {
    public RobotGroupStructureExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable FoldingDescriptor[] fold(@NotNull Document document) {
        RobotGroupHeader groupHeader = getGroupHeader();
        List<RobotExecutableStatement> executableStatements = getExecutableStatementList();
        PsiElement endElement = findChildByType(RobotTypes.END);
        if (endElement == null) {
            return null;
        }
        List<FoldingDescriptor> foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForBlockStructure(this,
                                                                                                                            groupHeader,
                                                                                                                            executableStatements,
                                                                                                                            document,
                                                                                                                            endElement.getTextRange().getStartOffset());
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : null;
    }
}
