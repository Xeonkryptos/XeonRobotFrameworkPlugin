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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordInvokable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Parameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionGroup;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PyElementDeprecatedVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PyElementParentTraversalVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class RobotKeywordAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof KeywordInvokable keywordInvokable)) {
            return;
        }

        PsiReference reference = element.getReference();
        PsiElement resolvedElement = reference.resolve();
        boolean isResolved = resolvedElement != null;
        if (!isResolved && element.getNode().getElementType() != RobotTokenTypes.SYNTAX_MARKER) {
            VariableDefinitionGroup variableDefinitionGroup = PsiTreeUtil.getParentOfType(element, VariableDefinitionGroup.class);
            // Don't mark it as an unresolved keyword as it is an argument of a variable definition (marked by RobotVariableArgumentAnnotator).
            // While lexing and parsing it isn't possible to differentiate between a keyword statement and an argument. Better say it as a keyword statement
            // and change the highlighting and suppress every message than to try to modify the tree after lexing/parsing
            if (variableDefinitionGroup == null) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.not-found"))
                      .highlightType(ProblemHighlightType.ERROR)
                      .range(element)
                      .create();
            }
        } else if (resolvedElement instanceof PyElement pyElement) {
            PyElementDeprecatedVisitor pyElementDeprecatedVisitor = new PyElementDeprecatedVisitor();
            PyElementVisitor pyElementParentTraversalVisitor = new PyElementParentTraversalVisitor(pyElementDeprecatedVisitor);
            pyElement.accept(pyElementParentTraversalVisitor);

            if (pyElementDeprecatedVisitor.isDeprecated()) {
                holder.newSilentAnnotation(HighlightSeverity.WARNING).range(element).highlightType(ProblemHighlightType.LIKE_DEPRECATED).create();
            }
        }

        KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(keywordInvokable, KeywordStatement.class);
        if (keywordStatement != null && !keywordStatement.allRequiredParametersArePresent()) {
            Set<String> definedParameterNames = keywordStatement.getParameters().stream().map(Parameter::getName).collect(Collectors.toSet());
            long expectedCount = keywordStatement.getAvailableParameters()
                                                 .stream()
                                                 .filter(parameter -> !parameter.hasDefaultValue())
                                                 .map(DefinedParameter::getLookup)
                                                 .filter(paramNames -> !definedParameterNames.contains(paramNames))
                                                 .count();
            int argumentCount = keywordStatement.getArguments().size();
            expectedCount -= argumentCount;
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.parameters.missing", expectedCount))
                  .highlightType(ProblemHighlightType.ERROR)
                  .range(keywordInvokable)
                  .create();
        }
    }
}
