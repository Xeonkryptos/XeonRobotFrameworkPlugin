package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.BracketSetting;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotDocumentationEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<BracketSetting> {

    @Nullable
    @Override
    protected BracketSetting getExpectedElement(@Nullable PsiElement element, int lineStartOffset) {
        return element instanceof BracketSetting bracketSetting ? bracketSetting : PsiTreeUtil.getParentOfType(element, BracketSetting.class);
    }

    @Override
    protected boolean isMultilineSupportedFor(@NotNull BracketSetting element) {
        return element.isDocumentation();
    }
}
