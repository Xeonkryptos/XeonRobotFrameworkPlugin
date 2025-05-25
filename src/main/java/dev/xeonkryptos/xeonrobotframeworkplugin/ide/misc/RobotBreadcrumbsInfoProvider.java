package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.lang.Language;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Heading;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinitionId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordInvokable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionGroup;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class RobotBreadcrumbsInfoProvider implements BreadcrumbsProvider {

    private final Language[] supportedLanguages = new Language[] { RobotLanguage.INSTANCE };

    @Override
    public Language[] getLanguages() {
        return supportedLanguages;
    }

    @Override
    public boolean acceptElement(@NotNull PsiElement element) {
        return element instanceof Heading || element instanceof KeywordDefinition || element instanceof VariableDefinitionGroup
               || element instanceof KeywordStatement || (element instanceof KeywordInvokable keywordInvokable
                                                          && !(keywordInvokable.getParent() instanceof KeywordStatement));
    }

    @Override
    public @Nullable Icon getElementIcon(@NotNull PsiElement element) {
        return element.getIcon(0);
    }

    @NotNull
    @Override
    public String getElementInfo(@NotNull PsiElement element) {
        RobotStatement robotStatement = (RobotStatement) element;
        String presentableText = robotStatement.getPresentableText();
        return StringUtil.shortenTextWithEllipsis(presentableText, 32, 0, true);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean acceptStickyElement(@NotNull PsiElement element) {
        return element instanceof Heading || element instanceof KeywordDefinition || element instanceof KeywordDefinitionId
               || element instanceof VariableDefinitionGroup || element instanceof VariableDefinition || element instanceof VariableDefinitionId
               || element instanceof KeywordStatement || element instanceof KeywordInvokable;
    }
}
