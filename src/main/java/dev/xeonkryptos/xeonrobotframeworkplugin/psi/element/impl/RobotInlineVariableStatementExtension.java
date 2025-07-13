package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import org.jetbrains.annotations.NotNull;

public abstract class RobotInlineVariableStatementExtension extends RobotPsiElementBase implements RobotInlineVariableStatement {

    public RobotInlineVariableStatementExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getNameIdentifier() {
        return getVariableDefinition().getVariable();
    }
}
