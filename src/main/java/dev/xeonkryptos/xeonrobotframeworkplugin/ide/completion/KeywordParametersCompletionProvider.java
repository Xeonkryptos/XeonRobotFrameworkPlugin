package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.RobotTailTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

class KeywordParametersCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        RobotKeywordCall keyword = PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), RobotKeywordCall.class);
        if (keyword != null) {
            addKeywordParameters(keyword, result);
        }
    }

    private void addKeywordParameters(@NotNull RobotKeywordCall keywordStatement, @NotNull CompletionResultSet resultSet) {
        Collection<DefinedParameter> availableParameters = keywordStatement.getAvailableParameters();
        Set<String> arguments = keywordStatement.getParameterList().stream().map(RobotParameter::getName).collect(Collectors.toSet());
        availableParameters.removeIf(parameter -> arguments.contains(parameter.getLookup()) || parameter.isKeywordContainer());

        for (DefinedParameter parameter : availableParameters) {
            CompletionProviderUtils.addLookupElement(parameter, Nodes.Parameter, !parameter.hasDefaultValue(), RobotTailTypes.ASSIGNMENT_TAIL_TYPE, resultSet)
                                   .ifPresent(lookupElement -> {
                                       lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                                       lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.PARAMETER);
                                   });
        }
    }
}
