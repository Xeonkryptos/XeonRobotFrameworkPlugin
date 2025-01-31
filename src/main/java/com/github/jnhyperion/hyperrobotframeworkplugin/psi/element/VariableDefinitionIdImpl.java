package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class VariableDefinitionIdImpl extends RobotPsiElementBase implements VariableDefinitionId {

    public VariableDefinitionIdImpl(@NotNull ASTNode node) {
        super(node);
    }
}
