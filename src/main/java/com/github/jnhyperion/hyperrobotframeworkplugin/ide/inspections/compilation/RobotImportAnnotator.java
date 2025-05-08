package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public class RobotImportAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof Import importElement)) {
            return;
        }

        PsiElement[] children = importElement.getChildren();
        if (children.length != 0 && children[0] == element) {
            PsiElement child = children[0];
            PsiReference reference = child.getReference();
            Application application = ApplicationManager.getApplication();
            if (reference == null || (application.runReadAction((Computable<? extends PsiElement>) reference::resolve)) == null) {
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.import.not-found")).range(child).create();
            }
        }
    }
}
