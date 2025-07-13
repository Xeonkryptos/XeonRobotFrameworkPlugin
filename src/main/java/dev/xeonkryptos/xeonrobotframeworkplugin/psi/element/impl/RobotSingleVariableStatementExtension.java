package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import org.jetbrains.annotations.NotNull;

public abstract class RobotSingleVariableStatementExtension extends RobotPsiElementBase implements RobotSingleVariableStatement {

    public RobotSingleVariableStatementExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getNameIdentifier() {
        return getVariableDefinition().getVariable();
    }
}
