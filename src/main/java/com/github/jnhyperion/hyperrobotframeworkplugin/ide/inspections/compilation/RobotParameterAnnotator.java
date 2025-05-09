package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedParameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Parameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterId;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Variable;
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
