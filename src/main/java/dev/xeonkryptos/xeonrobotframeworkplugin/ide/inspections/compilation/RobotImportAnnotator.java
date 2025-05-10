package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Import;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import org.jetbrains.annotations.NotNull;

public class RobotImportAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!element.isValid() || !(element instanceof Import importElement)) {
            return;
        }

        PsiElement[] children = importElement.getChildren();
        if (children.length != 0) {
            PsiElement child = children[0];
            if (!(child.getFirstChild() instanceof Variable)) {
                PsiReference reference = child.getReference();
                if (reference == null || reference.resolve() == null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.import.not-found")).range(child).create();
                } else {
                    holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(RobotHighlighter.IMPORT_ARGUMENT).range(child).create();
                }
            }
        }
    }
}
