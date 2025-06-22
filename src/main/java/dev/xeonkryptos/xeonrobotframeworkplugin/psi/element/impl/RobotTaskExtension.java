package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotTaskExtension extends ASTWrapperPsiElement implements RobotTaskStatement {

    public RobotTaskExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return getTaskId();
    }
}
