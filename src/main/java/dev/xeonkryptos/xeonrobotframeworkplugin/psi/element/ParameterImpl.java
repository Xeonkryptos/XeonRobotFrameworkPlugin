package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class ParameterImpl extends RobotPsiElementBase implements Parameter {

    public ParameterImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getParameterName() {
        ParameterId parameterId = getNameIdentifier();
        return parameterId.getName();
    }

    @NotNull
    @Override
    public ParameterId getNameIdentifier() {
        return PsiTreeUtil.getRequiredChildOfType(this, ParameterId.class);
    }
}
