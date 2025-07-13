package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotBreadcrumbsInfoElementCollector;
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
        RobotBreadcrumbsInfoElementCollector breadcrumbsInfoCollector = new RobotBreadcrumbsInfoElementCollector();
        element.accept(breadcrumbsInfoCollector);
        return breadcrumbsInfoCollector.isIncludeInBreadcrumbs();
    }

    @Override
    public @Nullable Icon getElementIcon(@NotNull PsiElement element) {
        return element.getIcon(0);
    }

    @NotNull
    @Override
    public String getElementInfo(@NotNull PsiElement element) {
        NavigatablePsiElement navigatablePsiElement = (NavigatablePsiElement) element;
        ItemPresentation presentation = navigatablePsiElement.getPresentation();
        assert presentation != null;
        String presentableText = presentation.getPresentableText();
        assert presentableText != null;
        return StringUtil.shortenTextWithEllipsis(presentableText, 32, 0, true);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean acceptStickyElement(@NotNull PsiElement element) {
        RobotBreadcrumbsInfoElementCollector breadcrumbsInfoCollector = new RobotBreadcrumbsInfoElementCollector();
        element.accept(breadcrumbsInfoCollector);
        return breadcrumbsInfoCollector.isSticky();
    }

}
