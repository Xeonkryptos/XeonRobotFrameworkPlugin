package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Parameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

class VariableCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getPosition();
        Parameter parameter = PsiTreeUtil.getParentOfType(psiElement, Parameter.class);
        if (parameter != null) {
            // In parameter context, the prefix usually contains the parameter name, too. For finding and filtering variable names, we need to
            // remove the parameter name from the prefix.
            String prefix = result.getPrefixMatcher().getPrefix();
            String newPrefix;
            if (prefix.startsWith("=")) {
                newPrefix = prefix.substring(1);
            } else if (prefix.startsWith(parameter.getParameterName() + "=")) {
                int parameterDefinitionLength = (parameter.getParameterName() + "=").length();
                newPrefix = prefix.substring(parameterDefinitionLength);
            } else {
                newPrefix = prefix;
            }
            result = result.withPrefixMatcher(newPrefix);
        }

        addDefinedVariablesFromImportedFiles(result, parameters.getOriginalFile(), psiElement);
        addDefinedVariablesFromKeyword(result, psiElement);
    }

    private void addDefinedVariablesFromImportedFiles(@NotNull CompletionResultSet resultSet, @NotNull PsiFile file, @Nullable PsiElement element) {
        RobotFile robotFile = (RobotFile) file;
        addDefinedVariables(robotFile.getDefinedVariables(),
                            resultSet,
                            element).forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE,
                                                                                        RobotLookupElementType.VARIABLE));
        boolean allowTransitiveImports = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
        for (KeywordFile importedFile : robotFile.getImportedFiles(allowTransitiveImports)) {
            if (importedFile.getImportType() == ImportType.VARIABLES || importedFile.getImportType() == ImportType.RESOURCE) {
                addDefinedVariables(importedFile.getDefinedVariables(),
                                    resultSet,
                                    element).forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE,
                                                                                                RobotLookupElementType.VARIABLE));
            }
        }
    }

    private void addDefinedVariablesFromKeyword(@NotNull CompletionResultSet resultSet, @NotNull PsiElement element) {
        KeywordDefinition keywordDefinition = PsiTreeUtil.getParentOfType(element, KeywordDefinition.class);
        if (keywordDefinition != null) {
            for (DefinedVariable variable : keywordDefinition.getDeclaredVariables()) {
                CompletionProviderUtils.addLookupElement(variable, Nodes.Variable, false, TailTypes.noneType(), resultSet).ifPresent(lookupElement -> {
                    lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                    lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
                });
            }
        }
    }

    private Collection<LookupElement> addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                                          @NotNull CompletionResultSet resultSet,
                                                          @Nullable PsiElement element) {
        return addDefinedVariables(variables, resultSet, element, TailTypes.noneType());
    }

    private Collection<LookupElement> addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                                          @NotNull CompletionResultSet resultSet,
                                                          @Nullable PsiElement element,
                                                          @NotNull TailType tailType) {
        return variables.stream()
                        .filter(variable -> variable.isInScope(element))
                        .map(variable -> CompletionProviderUtils.addLookupElement(variable, Nodes.Variable, false, tailType, resultSet))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
    }
}
