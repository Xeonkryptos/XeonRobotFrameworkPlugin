package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotParameterReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public class ParameterIdImpl extends RobotPsiElementBase implements ParameterId {

    private String name;

    public ParameterIdImpl(@NotNull ASTNode node) {
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

    @Override
    public PsiReference getReference() {
        return new RobotParameterReference(this);
    }
}
