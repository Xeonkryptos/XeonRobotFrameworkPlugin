package com.github.jnhyperion.hyperrobotframeworkplugin.ide.actionhandler;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.BracketSetting;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotDocumentationEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<BracketSetting> {

    @Override
    protected BracketSetting getExpectedElement(@Nullable PsiElement element) {
        return element instanceof BracketSetting bracketSetting ? bracketSetting : PsiTreeUtil.getParentOfType(element, BracketSetting.class);
    }

    @Override
    protected boolean isMultilineSupportedFor(@NotNull BracketSetting element) {
        return element.isDocumentation();
    }
}
