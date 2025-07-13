package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class RobotVariableStatementExtension extends RobotPsiElementBase implements RobotVariableStatement {

    public RobotVariableStatementExtension(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.VARIABLE;
    }
}
