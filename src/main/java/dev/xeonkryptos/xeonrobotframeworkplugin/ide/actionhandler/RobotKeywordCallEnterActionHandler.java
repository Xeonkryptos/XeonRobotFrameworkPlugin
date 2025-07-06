package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordCallEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<RobotKeywordCall> {

    @Nullable
    @Override
    protected RobotKeywordCall getExpectedElement(@Nullable PsiElement element, int lineStartOffset) {
        if (element instanceof RobotKeywordCall keywordStatement) {
            return keywordStatement;
        }
        RobotKeywordCall keywordStatement = PsiTreeUtil.getParentOfType(element, RobotKeywordCall.class);
        if (keywordStatement != null) {
            return keywordStatement;
        }
        if (element instanceof PsiComment) {
            PsiElement prevSibling = element.getPrevSibling();
            if (prevSibling instanceof RobotKeywordCall foundStatement) {
                return foundStatement;
            }
            while (prevSibling instanceof PsiWhiteSpace && prevSibling.getTextRange().getStartOffset() >= lineStartOffset) {
                prevSibling = prevSibling.getPrevSibling();
            }
            return prevSibling instanceof RobotKeywordCall foundStatement ? foundStatement : PsiTreeUtil.getParentOfType(prevSibling, RobotKeywordCall.class);
        }
        // Child in case of a Variable Definition
        return PsiTreeUtil.findChildOfType(element, RobotKeywordCall.class);
    }

    @Override
    protected boolean isMultilineSupportedFor(@NotNull RobotKeywordCall element) {
        return true;
    }
}
