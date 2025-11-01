package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.highlight;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.DeprecationInspector;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordDeprecationAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotKeywordCallName robotKeywordCallName)) {
            return;
        }

        PsiReference reference = robotKeywordCallName.getReference();
        PsiElement resolvedElement = reference.resolve();
        if (resolvedElement != null && DeprecationInspector.isDeprecated(resolvedElement)) {
            holder.newSilentAnnotation(HighlightSeverity.WARNING).range(element).highlightType(ProblemHighlightType.LIKE_DEPRECATED).create();
        }
    }
}
