package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotParameterReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public class ParameterIdImpl extends RobotPsiElementBase implements ParameterId {

    public ParameterIdImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public PsiReference getReference() {
        return new RobotParameterReference(this);
    }
}
