package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordInvokable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PyElementDeprecatedVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PyElementParentTraversalVisitor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!element.isValid() || !(element instanceof KeywordInvokable)) {
            return;
        }
        PsiReference reference = element.getReference();
        PsiElement resolvedElement = reference != null ? reference.resolve() : null;
        boolean isResolved = resolvedElement != null;
        if (!isResolved && element.getNode().getElementType() != RobotTokenTypes.SYNTAX_MARKER) {
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
