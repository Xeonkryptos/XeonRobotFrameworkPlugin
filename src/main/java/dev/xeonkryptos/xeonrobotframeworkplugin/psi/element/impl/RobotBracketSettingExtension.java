package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotBracketSetting;
import org.jetbrains.annotations.NotNull;

public abstract class RobotBracketSettingExtension extends RobotPsiElementBase implements RobotBracketSetting {

    public RobotBracketSettingExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement getNameIdentifier() {
        return getBracketSettingId();
    }
}
