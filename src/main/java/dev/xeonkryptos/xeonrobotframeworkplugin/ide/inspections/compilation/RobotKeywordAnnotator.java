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
import org.jetbrains.annotations.NotNull;

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
            long expectedCount = keywordCall.getAvailableParameters()
                                            .stream()
                                            .filter(parameter -> !parameter.hasDefaultValue())
                                            .map(DefinedParameter::getLookup)
                                            .filter(paramNames -> !definedParameterNames.contains(paramNames))
                                            .count();
            int argumentCount = keywordCall.getPositionalArgumentList().size();
            expectedCount -= argumentCount;
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.parameters.missing", expectedCount))
                  .highlightType(ProblemHighlightType.ERROR)
                  .range(robotKeywordCallId)
                  .create();
        }
    }
}
