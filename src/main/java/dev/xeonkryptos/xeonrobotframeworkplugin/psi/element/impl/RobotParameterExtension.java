package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import org.jetbrains.annotations.NotNull;

public abstract class RobotParameterExtension extends RobotPsiElementBase implements RobotParameter {

    public RobotParameterExtension(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getNameIdentifier().getName();
    }
}
