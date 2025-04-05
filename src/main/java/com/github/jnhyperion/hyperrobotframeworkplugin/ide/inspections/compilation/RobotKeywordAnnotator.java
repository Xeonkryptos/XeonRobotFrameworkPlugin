package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordAnnotator implements Annotator, DumbAware {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof KeywordInvokable)) {
            return;
        }
        PsiReference reference = element.getReference();
        boolean isResolved = reference != null && reference.resolve() != null;

        if (!isResolved && element.getNode().getElementType() == RobotTokenTypes.SYNTAX_MARKER) {
            return;
        }

        if (!isResolved) {
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.not-found"))
                  .range(element)
                  .create();
        }
    }
}
