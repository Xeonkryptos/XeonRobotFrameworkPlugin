package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class ParameterIdImpl extends RobotPsiElementBase implements ParameterId {

    public ParameterIdImpl(@NotNull ASTNode node) {
        super(node);
    }
}
