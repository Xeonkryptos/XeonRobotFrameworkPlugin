package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public abstract class RobotTestCaseExtension extends RobotPsiElementBase implements RobotTestCaseStatement {

    public RobotTestCaseExtension(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.MODELS;
    }
}
