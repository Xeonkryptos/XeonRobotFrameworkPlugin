package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotTailTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCallArgumentsContainer;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

class KeywordParametersCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        RobotCallArgumentsContainer container = PsiTreeUtil.getParentOfType(position, RobotKeywordCall.class, RobotTemplateArguments.class);
        if (container != null) {
            addKeywordParameters(container, result);
        }
    }

    private void addKeywordParameters(@NotNull RobotCallArgumentsContainer callArgumentsContainer, @NotNull CompletionResultSet resultSet) {
        Collection<DefinedParameter> missingParameters = callArgumentsContainer.computeMissingParameters();
        for (DefinedParameter parameter : missingParameters) {
            boolean requiredParameter = !parameter.hasDefaultValue();
            CompletionProviderUtils.addLookupElement(parameter, Nodes.Parameter, requiredParameter, RobotTailTypes.ASSIGNMENT_TAIL_TYPE, resultSet)
                                   .ifPresent(lookupElement -> {
                                       lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                                       lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.PARAMETER);
                                       lookupElement.putUserData(CompletionKeys.ROBOT_REQUIRED_VALUE, requiredParameter);
                                   });
        }
    }
}
