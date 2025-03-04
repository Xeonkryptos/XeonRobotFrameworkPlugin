package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParameterImpl extends RobotPsiElementBase implements Parameter {

    public ParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getParameterName() {
        ParameterId parameterId = PsiTreeUtil.getChildOfType(this, ParameterId.class);
        assert parameterId != null;
        return parameterId.getPresentableText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return PsiTreeUtil.findChildOfType(this, ParameterId.class);
    }
}
