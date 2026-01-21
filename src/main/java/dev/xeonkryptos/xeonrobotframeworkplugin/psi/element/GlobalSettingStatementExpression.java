package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface GlobalSettingStatementExpression {

    default String getSettingName() {
        return getNameElement().getText();
    }

    @NotNull
    PsiElement getNameElement();
}
