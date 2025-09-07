package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.openapi.project.Project;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCallArgumentsContainer;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;

import java.text.Collator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

class KeywordParameterEvaluator {

    private KeywordParameterEvaluator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Collection<String> computeMissingRequiredParameters(RobotKeywordCall keywordCall, RobotCallArgumentsContainer container) {
        Project project = keywordCall.getProject();
        Collection<String> definedParameters = container.getDefinedParameterNames();
        Collection<DefinedParameter> requiredParameterNames = keywordCall.getAvailableParameters()
                                                                         .stream()
                                                                         .filter(param -> !param.hasDefaultValue() && !param.isKeywordContainer())
                                                                         .collect(Collectors.toCollection(LinkedHashSet::new));
        removeMatchingParameters(definedParameters, requiredParameterNames, project);
        int missingParameterCount = requiredParameterNames.size() - keywordCall.getPositionalArgumentList().size();
        if (missingParameterCount <= 0) {
            return List.of();
        }
        return requiredParameterNames.stream().map(DefinedParameter::getLookup).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Collection<DefinedParameter> computeMissingParameters(RobotKeywordCall keywordCall, RobotCallArgumentsContainer container) {
        Project project = keywordCall.getProject();
        Collection<String> definedParameters = container.getDefinedParameterNames();
        Collection<DefinedParameter> availableParameters = new LinkedHashSet<>(keywordCall.getAvailableParameters());
        removeMatchingParameters(definedParameters, availableParameters, project);
        return availableParameters;
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
}
