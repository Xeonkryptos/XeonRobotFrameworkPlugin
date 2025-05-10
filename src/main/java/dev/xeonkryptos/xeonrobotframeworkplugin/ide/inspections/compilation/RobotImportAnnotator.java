package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Import;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public class RobotImportAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!element.isValid() || !(element instanceof Import importElement)) {
            return;
        }

        PsiElement[] children = importElement.getChildren();
        if (children.length != 0 && children[0] == element) {
            PsiElement child = children[0];
            PsiReference reference = child.getReference();
            if (reference == null || reference.resolve() == null) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.import.not-found")).range(child).create();
            }
        }
    }
}
