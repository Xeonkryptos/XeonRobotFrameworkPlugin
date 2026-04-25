package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCallArgumentsContainer;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

class KeywordParameterEvaluator {

    private KeywordParameterEvaluator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Collection<String> computeMissingRequiredParameters(RobotKeywordCall keywordCall, RobotCallArgumentsContainer container) {
        MissingParametersResult result = computeMissingParametersInternal(keywordCall, container, null, true);
        if (result.missingPositional.isEmpty() && result.missingKeywords.isEmpty()) {
            return List.of();
        }
        Collection<String> missingParameterNames = new LinkedHashSet<>();
        for (DefinedParameter param : result.missingPositional) {
            missingParameterNames.add(param.getLookup());
        }
        for (DefinedParameter param : result.missingKeywords) {
            missingParameterNames.add(param.getLookup());
        }
        return missingParameterNames;
    }

    public static Collection<DefinedParameter> computeMissingParameters(RobotKeywordCall keywordCall, RobotCallArgumentsContainer container, PsiElement ignorableElement) {
        MissingParametersResult result = computeMissingParametersInternal(keywordCall, container, ignorableElement, false);
        Collection<DefinedParameter> allMissing = new LinkedHashSet<>(result.missingPositional);
        allMissing.addAll(result.missingKeywords);
        return allMissing;
    }

    private static MissingParametersResult computeMissingParametersInternal(RobotKeywordCall keywordCall, RobotCallArgumentsContainer container, PsiElement ignorableElement, boolean countOnly) {
        Project project = keywordCall.getProject();
        Collection<String> definedParameterNames = new LinkedHashSet<>(container.getDefinedParameterNames());
        List<DefinedParameter> availableParameters = keywordCall.getAvailableParameters()
                                                                .stream()
                                                                .filter(param -> !(param.isPositionalContainer() || param.isKeywordContainer()))
                                                                .collect(Collectors.toCollection(ArrayList::new));

        OptionalInt positionalContainerEndOpt = keywordCall.getPositionalArgumentsOnlyEndIndex();
        OptionalInt keywordsOnlyStartOpt = keywordCall.getStartOfKeywordsOnlyIndex();

        int positionalContainerEndIndex = positionalContainerEndOpt.orElse(-1);
        int keywordsOnlyStartIndex = keywordsOnlyStartOpt.orElse(availableParameters.size());

        List<DefinedParameter> positionalOnlyRequired = availableParameters.stream()
                                                                           .filter(param -> param.isPositionalOnly() && !param.hasDefaultValue())
                                                                           .collect(Collectors.toCollection(ArrayList::new));

        List<DefinedParameter> keywordRequired = availableParameters.stream()
                                                                    .filter(param -> !param.isPositionalOnly() && !param.hasDefaultValue()
                                                                                     && availableParameters.indexOf(param) >= positionalContainerEndIndex)
                                                                    .collect(Collectors.toCollection(ArrayList::new));

        int positionalArgumentsCount = countPositionalArguments(container, keywordsOnlyStartIndex, ignorableElement);

        boolean hasPositionalContainer = positionalContainerEndIndex >= 0;
        if (hasPositionalContainer && positionalArgumentsCount > positionalContainerEndIndex) {
            positionalArgumentsCount = positionalContainerEndIndex;
        }

        List<DefinedParameter> missingPositional;
        if (countOnly) {
            int positionalOnlyNeeded = Math.max(0, positionalOnlyRequired.size() - positionalArgumentsCount);
            missingPositional = positionalOnlyRequired.stream().limit(positionalOnlyNeeded).collect(Collectors.toList());
        } else {
            int positionalSatisfied = Math.min(positionalArgumentsCount, positionalOnlyRequired.size());
            missingPositional = positionalOnlyRequired.subList(positionalSatisfied, positionalOnlyRequired.size());
        }

        removeMatchingParameters(definedParameterNames, keywordRequired, project);
        List<DefinedParameter> missingKeywords = keywordRequired.stream().filter(param -> !definedParameterNames.contains(param.getLookup())).toList();

        return new MissingParametersResult(missingPositional, missingKeywords);
    }

    private static int countPositionalArguments(RobotCallArgumentsContainer container, int keywordsOnlyStartIndex, PsiElement ignorableElement) {
        Collection<RobotArgument> allArguments = container.getAllCallArguments();
        int count = 0;
        int index = 0;
        for (RobotArgument arg : allArguments) {
            if (index >= keywordsOnlyStartIndex) {
                break;
            }
            if (arg == ignorableElement) {
                continue;
            }
            if (!(arg instanceof RobotParameter) || ((RobotParameter) arg).isFakeParameter()) {
                count++;
            }
            index++;
        }
        return count;
    }

    private static void removeMatchingParameters(Collection<String> definedParameters, Collection<? extends DefinedParameter> availableParameters, Project project) {
        RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(project);
        Collator parameterNameCollator = robotOptionsProvider.getParameterNameCollator();

        Iterator<String> definedParamsIterator = definedParameters.iterator();
        while (definedParamsIterator.hasNext()) {
            String definedParamName = definedParamsIterator.next();
            Iterator<? extends DefinedParameter> availableParamsIterator = availableParameters.iterator();
            while (availableParamsIterator.hasNext()) {
                DefinedParameter availableParameter = availableParamsIterator.next();
                String availableParameterName = availableParameter.getLookup();
                if (parameterNameCollator.equals(definedParamName, availableParameterName)) {
                    definedParamsIterator.remove();
                    availableParamsIterator.remove();
                    break;
                }
            }
        }
    }

    private record MissingParametersResult(List<DefinedParameter> missingPositional, List<DefinedParameter> missingKeywords) {}
}
