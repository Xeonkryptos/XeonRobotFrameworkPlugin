package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLanguage;
import org.jetbrains.annotations.NotNull;

public abstract class RobotLanguageExtension extends RobotPsiElementBase implements RobotLanguage {

    public RobotLanguageExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        throw new IncorrectOperationException("Renaming operation is not supported for RobotLanguage elements.");
    }
}
