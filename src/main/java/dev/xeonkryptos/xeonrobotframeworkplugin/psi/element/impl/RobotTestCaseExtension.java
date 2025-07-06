package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import org.jetbrains.annotations.NotNull;

public abstract class RobotTestCaseExtension extends RobotPsiElementBase implements RobotTestCaseStatement {

    public RobotTestCaseExtension(@NotNull ASTNode node) {
        super(node);
    }
}
