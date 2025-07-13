package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotLocalSettingEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<RobotLocalSetting> {

    @Nullable
    @Override
    protected RobotLocalSetting getExpectedElement(@Nullable PsiElement element, int lineStartOffset) {
        if (element instanceof RobotLocalSetting bracketSetting) {
            return bracketSetting;
        }
        RobotLocalSetting bracketSetting = PsiTreeUtil.getParentOfType(element, RobotLocalSetting.class);
        if (bracketSetting != null) {
            return bracketSetting;
        }
        if (element instanceof PsiComment) {
            PsiElement prevSibling = element.getPrevSibling();
            if (prevSibling instanceof RobotLocalSetting foundSetting) {
                return foundSetting;
            }
            while (prevSibling instanceof PsiWhiteSpace && prevSibling.getTextRange().getStartOffset() >= lineStartOffset) {
                prevSibling = prevSibling.getPrevSibling();
            }
            return prevSibling instanceof RobotLocalSetting foundSetting ? foundSetting : PsiTreeUtil.getParentOfType(prevSibling, RobotLocalSetting.class);
        }
        return null;
    }

    @Override
    protected boolean isMultilineSupportedFor(@NotNull RobotLocalSetting element) {
        return true;
    }
}
