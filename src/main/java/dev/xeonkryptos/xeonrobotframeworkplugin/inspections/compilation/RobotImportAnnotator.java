package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotImportElementIdentifier;
import org.jetbrains.annotations.NotNull;

public class RobotImportAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        RobotImportElementIdentifier importElementIdentifier = new RobotImportElementIdentifier();
        element.accept(importElementIdentifier);
        if (!importElementIdentifier.isImportElement()) {
            return;
        }

        RobotPositionalArgument positionalArgument = importElementIdentifier.getPositionalArgument();
        PsiReference reference = positionalArgument.getReference();
        if (reference.resolve() == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.import.not-found"))
                  .highlightType(ProblemHighlightType.ERROR)
                  .range(positionalArgument)
                  .create();
        } else {
            holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).textAttributes(RobotHighlighter.IMPORT_ARGUMENT).range(positionalArgument).create();
        }
    }
}
