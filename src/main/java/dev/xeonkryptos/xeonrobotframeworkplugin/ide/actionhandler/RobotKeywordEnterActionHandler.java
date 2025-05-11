package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<KeywordStatement> {

    @Nullable
    @Override
    protected KeywordStatement getExpectedElement(@Nullable PsiElement element, int lineStartOffset) {
        if (element instanceof KeywordStatement keywordStatement) {
            return keywordStatement;
        }
        KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(element, KeywordStatement.class);
        if (keywordStatement != null) {
            return keywordStatement;
        }
        if (element instanceof PsiComment) {
            PsiElement prevSibling = element.getPrevSibling();
            if (prevSibling instanceof KeywordStatement) {
                return (KeywordStatement) prevSibling;
            } else {
                while (prevSibling instanceof PsiWhiteSpace && prevSibling.getTextRange().getStartOffset() >= lineStartOffset) {
                    prevSibling = prevSibling.getPrevSibling();
                }
                return prevSibling instanceof KeywordStatement foundStatement ?
                       foundStatement :
                       PsiTreeUtil.getParentOfType(prevSibling, KeywordStatement.class);
            }
        }
        return PsiTreeUtil.findChildOfType(element, KeywordStatement.class);
    }

    @Override
    protected boolean isMultilineSupportedFor(@NotNull KeywordStatement element) {
        return true;
    }
}
