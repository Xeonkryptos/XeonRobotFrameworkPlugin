package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class NamedArgumentImpl extends RobotPsiElementBase implements NamedArgument {

    public NamedArgumentImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getParameterName() {
        ParameterId parameterId = PsiTreeUtil.getChildOfType(this, ParameterId.class);
        assert parameterId != null;
        return parameterId.getPresentableText();
    }
}
