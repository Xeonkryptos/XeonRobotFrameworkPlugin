package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

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
        allMissing.addAll(result.missingOptional);
        return allMissing;
    }

    private static MissingParametersResult computeMissingParametersInternal(RobotKeywordCall keywordCall, RobotCallArgumentsContainer container, PsiElement ignorableElement, boolean countOnly) {
        Collection<String> definedParameterNames = new LinkedHashSet<>(container.getDefinedParameterNames());
        List<DefinedParameter> availableParameters = keywordCall.getAvailableParameters()
                                                                .stream()
                                                                .filter(param -> !(param.isPositionalContainer() || param.isKeywordContainer()))
                                                                .collect(Collectors.toCollection(ArrayList::new));

        OptionalInt positionalContainerEndOpt = keywordCall.getPositionalArgumentsOnlyEndIndex();
        OptionalInt keywordsOnlyStartOpt = keywordCall.getStartOfKeywordsOnlyIndex();

        int positionalContainerEndIndex = positionalContainerEndOpt.orElse(-1);
        int keywordsOnlyStartIndex = keywordsOnlyStartOpt.orElse(availableParameters.size());

        List<DefinedParameter> positionalOnlyRequired = new ArrayList<>();
        List<DefinedParameter> normalRequired = new ArrayList<>();
        List<DefinedParameter> keywordOnlyRequired = new ArrayList<>();
        List<DefinedParameter> optional = new ArrayList<>();

        for (int i = 0; i < availableParameters.size(); i++) {
            DefinedParameter param = availableParameters.get(i);
            if (param.hasDefaultValue()) {
                optional.add(param);
            } else if (param.isPositionalOnly()) {
                positionalOnlyRequired.add(param);
            } else if (i >= keywordsOnlyStartIndex) {
                keywordOnlyRequired.add(param);
            } else {
                normalRequired.add(param);
            }
        }

        int positionalArgumentsCount = countPositionalArguments(container, keywordsOnlyStartIndex, ignorableElement);

        boolean hasPositionalContainer = positionalContainerEndIndex >= 0;
        if (hasPositionalContainer && positionalArgumentsCount > positionalContainerEndIndex) {
            positionalArgumentsCount = positionalContainerEndIndex;
        }

        List<DefinedParameter> allNonKeywordOnlyRequired = new ArrayList<>(positionalOnlyRequired);
        allNonKeywordOnlyRequired.addAll(normalRequired);

        int positionalSatisfied = Math.min(positionalArgumentsCount, allNonKeywordOnlyRequired.size());

        List<DefinedParameter> missingPositional;
        if (countOnly) {
            int positionalOnlyNeeded = Math.max(0, positionalOnlyRequired.size() - positionalArgumentsCount);
            missingPositional = positionalOnlyRequired.stream().limit(positionalOnlyNeeded).collect(Collectors.toList());
        } else {
            List<DefinedParameter> missingFromPositional = new ArrayList<>();
            for (int i = positionalSatisfied; i < allNonKeywordOnlyRequired.size(); i++) {
                missingFromPositional.add(allNonKeywordOnlyRequired.get(i));
            }
            missingPositional = missingFromPositional;
        }

        int normalSatisfiedByPositional = Math.max(0, positionalSatisfied - positionalOnlyRequired.size());

        List<DefinedParameter> missingKeywords = new ArrayList<>();
        Collator parameterNameCollator = RobotOptionsProvider.getInstance(keywordCall.getProject()).getParameterNameCollator();
        for (int i = normalSatisfiedByPositional; i < normalRequired.size(); i++) {
            DefinedParameter param = normalRequired.get(i);

            boolean found = false;
            for (String definedParameterName : definedParameterNames) {
                if (parameterNameCollator.equals(param.getLookup(), definedParameterName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingKeywords.add(param);
            }
        }
        findMatchingParameterNames(definedParameterNames, keywordOnlyRequired, parameterNameCollator, missingKeywords);
        List<DefinedParameter> optionalKeywords = new ArrayList<>();
        findMatchingParameterNames(definedParameterNames, optional, parameterNameCollator, optionalKeywords);

        return new MissingParametersResult(missingPositional, missingKeywords, optionalKeywords);
    }

    private static void findMatchingParameterNames(Collection<String> definedParameterNames, List<DefinedParameter> optional, Collator parameterNameCollator, List<DefinedParameter> missingKeywords) {
        for (DefinedParameter param : optional) {
            boolean found = false;
            for (String definedParameterName : definedParameterNames) {
                if (parameterNameCollator.equals(param.getLookup(), definedParameterName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingKeywords.add(param);
            }
        }
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

    private record MissingParametersResult(List<DefinedParameter> missingPositional, List<DefinedParameter> missingKeywords, List<DefinedParameter> missingOptional) {}
}
