package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import org.jetbrains.annotations.NotNull;

public class RobotUnresolvedKeywordAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotKeywordCallName robotKeywordCallName)) {
            return;
        }

        PsiReference reference = robotKeywordCallName.getReference();
        PsiElement resolvedElement = reference.resolve();
        boolean isResolved = resolvedElement != null;
        if (!isResolved) {
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.keyword.not-found")).range(element).create();
        }
    }
}
