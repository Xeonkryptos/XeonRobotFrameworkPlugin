package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class VariableDefinitionGroupImpl extends RobotPsiElementBase implements VariableDefinitionGroup {

    public VariableDefinitionGroupImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Collection<DefinedVariable> getDefinedVariables() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, VariableDefinition.class)
                          .stream()
                          .map(definition -> (DefinedVariable) definition)
                          .toList();
    }
}
