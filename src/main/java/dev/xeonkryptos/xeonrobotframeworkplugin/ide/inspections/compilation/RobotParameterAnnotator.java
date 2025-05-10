package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Parameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.ParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotParameterAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!element.isValid() || !(element instanceof Parameter parameter)) {
            return;
        }
        KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(parameter, KeywordStatement.class);
        if (keywordStatement != null) {
            keywordStatement.reset();
            Collection<DefinedParameter> availableParameters = keywordStatement.getAvailableParameters();
            Set<String> parameterNames = availableParameters.stream().map(DefinedParameter::getLookup).collect(Collectors.toSet());
            String parameterName = parameter.getParameterName();
            if (!parameterNames.contains(parameterName) && availableParameters.stream().noneMatch(DefinedParameter::isKeywordContainer)) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.parameter.not-found")).range(parameter).create();
            }
        }

        RobotStatement argument = PsiTreeUtil.findChildOfAnyType(parameter, PositionalArgument.class, Variable.class);
        if (argument == null) {
            holder.newAnnotation(HighlightSeverity.WARNING, RobotBundle.getMessage("annotation.keyword.parameter.value.not-found")).range(parameter).create();
        }

        ParameterId parameterId = PsiTreeUtil.getRequiredChildOfType(parameter, ParameterId.class);
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(parameterId).textAttributes(DefaultLanguageHighlighterColors.PARAMETER).create();
    }
}
