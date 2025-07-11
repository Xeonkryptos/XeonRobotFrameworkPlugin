package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.PyElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PyElementDeprecatedVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PyElementParentTraversalVisitor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotKeywordAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotKeywordCallId robotKeywordCallId)) {
            return;
        }

        PsiReference reference = robotKeywordCallId.getReference();
        PsiElement resolvedElement = reference.resolve();
        boolean isResolved = resolvedElement != null;
        if (!isResolved) {
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.not-found"))
                  .highlightType(ProblemHighlightType.ERROR)
                  .range(element)
                  .create();
        } else if (resolvedElement instanceof PyElement pyElement) {
            PyElementDeprecatedVisitor pyElementDeprecatedVisitor = new PyElementDeprecatedVisitor();
            PyElementVisitor pyElementParentTraversalVisitor = new PyElementParentTraversalVisitor(pyElementDeprecatedVisitor);
            pyElement.accept(pyElementParentTraversalVisitor);

            if (pyElementDeprecatedVisitor.isDeprecated()) {
                holder.newSilentAnnotation(HighlightSeverity.WARNING).range(element).highlightType(ProblemHighlightType.LIKE_DEPRECATED).create();
            }
        }

        RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(robotKeywordCallId, RobotKeywordCall.class);
        if (keywordCall != null && !keywordCall.allRequiredParametersArePresent()) {
            Set<String> definedParameterNames = keywordCall.getParameterList().stream().map(RobotParameter::getName).collect(Collectors.toSet());
            Set<String> mandatoryParameters = keywordCall.getAvailableParameters()
                                                         .stream()
                                                         .filter(parameter -> !parameter.hasDefaultValue())
                                                         .map(DefinedParameter::getLookup)
                                                         .collect(Collectors.toCollection(HashSet::new));
            Set<String> copy = new HashSet<>(definedParameterNames);
            definedParameterNames.removeAll(mandatoryParameters);
            mandatoryParameters.removeAll(copy);

            filterOutSimilarParametersByFuzzyLookup(mandatoryParameters, definedParameterNames);
            if (mandatoryParameters.isEmpty()) {
                return;
            }

            int expectedCount = mandatoryParameters.size() - keywordCall.getPositionalArgumentList().size();
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.parameters.missing", expectedCount))
                  .highlightType(ProblemHighlightType.GENERIC_ERROR)
                  .range(robotKeywordCallId)
                  .create();
        }
    }

    private static void filterOutSimilarParametersByFuzzyLookup(Set<String> mandatoryParameters, Set<String> definedParameterNames) {
        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
        Iterator<String> mandatoryParametersIterator = mandatoryParameters.iterator();
        while (mandatoryParametersIterator.hasNext()) {
            String mandatoryParameter = mandatoryParametersIterator.next();
            Iterator<String> definedParametersIterator = definedParameterNames.iterator();
            while (definedParametersIterator.hasNext()) {
                String definedParameterName = definedParametersIterator.next();
                int distance = levenshteinDistance.apply(definedParameterName, mandatoryParameter);
                if (distance != -1 && distance <= 5) {
                    definedParametersIterator.remove();
                    mandatoryParametersIterator.remove();
                    break;
                }
            }
        }
    }
}
