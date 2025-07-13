package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUnknownSettingStatementsGlobalSetting;
import org.jetbrains.annotations.NotNull;

public abstract class RobotUnknownSettingStatementsGlobalSettingExtension extends RobotPsiElementBase implements RobotUnknownSettingStatementsGlobalSetting {

    public RobotUnknownSettingStatementsGlobalSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        throw new IncorrectOperationException("Renaming operation is not supported for RobotUnknownSetting elements.");
    }
}
