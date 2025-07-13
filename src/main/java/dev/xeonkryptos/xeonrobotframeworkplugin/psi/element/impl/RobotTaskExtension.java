package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import org.jetbrains.annotations.NotNull;

public abstract class RobotTaskExtension extends RobotPsiElementBase implements RobotTaskStatement {

    public RobotTaskExtension(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getNameIdentifier().getName();
    }
}
