package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class VariableDefinitionIdImpl extends RobotPsiElementBase implements VariableDefinitionId {

    private String name;

    public VariableDefinitionIdImpl(@NotNull ASTNode node) {
        super(node);

        name = getPresentableText();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        name = getPresentableText();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }
}
