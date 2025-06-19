package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotKeywordCallExtension extends RobotPsiElementBase implements RobotKeywordCall {

    public RobotKeywordCallExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement getNameIdentifier() {
        return getKeywordCallId();
    }
}
