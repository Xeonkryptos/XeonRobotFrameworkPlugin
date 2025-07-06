package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import org.jetbrains.annotations.NotNull;

public abstract class RobotTemplateParameterExtension extends RobotPsiElementBase implements RobotTemplateParameter {

    public RobotTemplateParameterExtension(@NotNull ASTNode node) {
        super(node);
    }
}
