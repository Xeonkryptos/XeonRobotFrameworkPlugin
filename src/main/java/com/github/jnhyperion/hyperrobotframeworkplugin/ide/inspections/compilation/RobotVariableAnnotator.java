package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Variable;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotVariableAnnotator implements Annotator, DumbAware {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("^\\$\\{(.*)}$");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Variable variable) || variable.isEmpty() || variable.isNested()) {
            return;
        }
        PsiReference reference = element.getReference();
        Application application = ApplicationManager.getApplication();
        if (reference != null) {
            PsiElement resolvedReference = application.runReadAction((Computable<? extends PsiElement>) reference::resolve);
            if (resolvedReference != null) {
                return;
            }
        }

        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(element.getProject());
        String elementText = injectedLanguageManager.getUnescapedText(element);
        Matcher matcher = VARIABLE_PATTERN.matcher(elementText);
        if (matcher.matches()) {
            try {
                Double.parseDouble(matcher.group(1));
                return;
            } catch (NumberFormatException ignored) {
            }
        }

        if (element.isValid()) {
            KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(element, KeywordStatement.class);
            if (keywordStatement != null) {
                List<PositionalArgument> positionalArguments = application.runReadAction((Computable<? extends List<PositionalArgument>>) keywordStatement::getPositionalArguments);
                if (keywordStatement.getGlobalVariable() != null && positionalArguments.size() > 1 && element == positionalArguments.getFirst()) {
                    return;
                }
            }
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.variable.not-found")).range(element).create();
        }
    }
}
