package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordEnterActionHandler extends AbstractRobotSmartMultilineEnterActionHandler<KeywordStatement> {

    @Override
    protected KeywordStatement getExpectedElement(@Nullable PsiElement element) {
        if (element instanceof KeywordStatement keywordStatement) {
            return keywordStatement;
        }
        KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(element, KeywordStatement.class);
        if (keywordStatement != null) {
            return keywordStatement;
        }
        return PsiTreeUtil.findChildOfType(element, KeywordStatement.class);
    }

    @Override
    protected boolean isMultilineSupportedFor(@NotNull KeywordStatement element) {
        return true;
    }
}
