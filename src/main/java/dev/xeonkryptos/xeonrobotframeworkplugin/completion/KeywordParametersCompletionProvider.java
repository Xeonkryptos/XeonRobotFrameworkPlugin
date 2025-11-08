package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
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
import java.util.LinkedList;

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
        Collection<LookupElement> lookupElements = new LinkedList<>();
        for (DefinedParameter parameter : missingParameters) {
            boolean requiredParameter = !parameter.hasDefaultValue();
            CompletionProviderUtils.createLookupElement(parameter, Nodes.Parameter, requiredParameter, RobotTailTypes.ASSIGNMENT_TAIL_TYPE)
                                   .ifPresent(lookupElement -> {
                                       lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                                       lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.PARAMETER);
                                       lookupElement.putUserData(CompletionKeys.ROBOT_REQUIRED_VALUE, requiredParameter);
                                       lookupElements.add(lookupElement);
                                   });
        }
        resultSet.addAllElements(lookupElements);
    }
}
