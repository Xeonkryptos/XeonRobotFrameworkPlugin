package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.BracketSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotDocumentationEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<BracketSetting> {

    @Nullable
    @Override
    protected BracketSetting getExpectedElement(@Nullable PsiElement element, int lineStartOffset) {
        if (element instanceof BracketSetting bracketSetting) {
            return bracketSetting;
        }
        BracketSetting bracketSetting = PsiTreeUtil.getParentOfType(element, BracketSetting.class);
        if (bracketSetting != null) {
            return bracketSetting;
        }
        if (element instanceof PsiComment) {
            PsiElement prevSibling = element.getPrevSibling();
            if (prevSibling instanceof BracketSetting foundSetting) {
                return foundSetting;
            }
            while (prevSibling instanceof PsiWhiteSpace && prevSibling.getTextRange().getStartOffset() >= lineStartOffset) {
                prevSibling = prevSibling.getPrevSibling();
            }
            return prevSibling instanceof BracketSetting foundSetting ? foundSetting : PsiTreeUtil.getParentOfType(prevSibling, BracketSetting.class);
        }
        return null;
    }

    @Override
    protected boolean isMultilineSupportedFor(@NotNull BracketSetting element) {
        return element.isDocumentation();
    }
}
