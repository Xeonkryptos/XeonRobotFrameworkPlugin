package com.github.jnhyperion.hyperrobotframeworkplugin.ide.completion;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedParameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Parameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterId;
import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

class KeywordParametersCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        KeywordStatement keyword = PsiTreeUtil.getParentOfType(parameters.getPosition(), KeywordStatement.class);
        if (keyword != null) {
            PsiElement psiParent = parameters.getPosition().getParent();
            PsiElement superParent = psiParent.getParent();
            if (superParent instanceof Parameter) {
                PsiElement prevSibling = psiParent.getPrevSibling();
                if (prevSibling != null && "=".equals(prevSibling.getText())) {
                    return;
                }
                result = result.withPrefixMatcher("");
            }
            addKeywordParameters(keyword, result);
        }
    }

    private void addKeywordParameters(@NotNull KeywordStatement keywordStatement, @NotNull CompletionResultSet resultSet) {
        Collection<DefinedParameter> availableParameters = keywordStatement.getAvailableParameters();
        Set<String> arguments = keywordStatement.getParameters()
                                                .stream()
                                                .flatMap(parameter -> PsiTreeUtil.getChildrenOfTypeAsList(parameter, ParameterId.class).stream())
                                                .map(ParameterId::getName)
                                                .collect(Collectors.toSet());
        availableParameters.removeIf(parameter -> arguments.contains(parameter.getLookup()) || parameter.isKeywordContainer());

        TailType assignmentTailType = TailType.createSimpleTailType('=');
        for (DefinedParameter parameter : availableParameters) {
            CompletionProviderUtils.addLookupElement(parameter, Nodes.Parameter, !parameter.hasDefaultValue(), assignmentTailType, resultSet)
                                   .ifPresent(lookupElement -> {
                                       lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                                       lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.PARAMETER);
                                   });
        }
    }
}
