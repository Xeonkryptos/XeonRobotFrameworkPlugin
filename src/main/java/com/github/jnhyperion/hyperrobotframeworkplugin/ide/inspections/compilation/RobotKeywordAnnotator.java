package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.PyElementDeprecatedVisitor;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.PyElementParentTraversalVisitor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof KeywordInvokable)) {
            return;
        }
        PsiReference reference = element.getReference();
        Application application = ApplicationManager.getApplication();
        PsiElement resolvedElement = reference != null ? application.runReadAction((Computable<? extends PsiElement>) reference::resolve) : null;
        boolean isResolved = resolvedElement != null;

        if (!isResolved && element.getNode().getElementType() == RobotTokenTypes.SYNTAX_MARKER) {
            return;
        }

        if (!isResolved) {
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.not-found")).range(element).create();
        } else if (resolvedElement instanceof PyElement pyElement) {
            PyElementDeprecatedVisitor pyElementDeprecatedVisitor = new PyElementDeprecatedVisitor();
            PyElementVisitor pyElementParentTraversalVisitor = new PyElementParentTraversalVisitor(pyElementDeprecatedVisitor);
            pyElement.accept(pyElementParentTraversalVisitor);

            if (pyElementDeprecatedVisitor.isDeprecated()) {
                holder.newSilentAnnotation(HighlightSeverity.WARNING).range(element).highlightType(ProblemHighlightType.LIKE_DEPRECATED).create();
            }
        }
    }
}
