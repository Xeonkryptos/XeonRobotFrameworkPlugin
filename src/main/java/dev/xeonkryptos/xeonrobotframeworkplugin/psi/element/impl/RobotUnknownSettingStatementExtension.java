package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUnknownSettingStatements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotUnknownSettingStatementExtension extends ASTWrapperPsiElement implements RobotUnknownSettingStatements {

    public RobotUnknownSettingStatementExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getUnknownSettingStatementId();
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        throw new IncorrectOperationException("Renaming operation is not supported for RobotUnknownSetting elements.");
    }
}
