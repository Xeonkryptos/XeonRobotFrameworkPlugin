package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.DumbAware;
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
        if (!element.isValid() || !(element instanceof Variable variable) || variable.isEmpty() || variable.isNested()) {
            return;
        }
        PsiReference reference = element.getReference();
        if (reference != null && reference.resolve() != null) {
            return;
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
                List<PositionalArgument> positionalArguments = keywordStatement.getPositionalArguments();
                if (keywordStatement.getGlobalVariable() != null && positionalArguments.size() > 1 && element == positionalArguments.getFirst()) {
                    return;
                }
            }
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.variable.not-found")).range(element).create();
        }
    }
}
