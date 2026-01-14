package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import org.jetbrains.annotations.NotNull;

public class RobotUnresolvedKeywordAnnotator extends RobotAnnotator {

    @Override
    public void visitKeywordCallName(@NotNull RobotKeywordCallName keywordCallName) {
        PsiReference reference = keywordCallName.getReference();
        PsiElement resolvedElement = reference.resolve();
        boolean isResolved = resolvedElement != null;
        if (!isResolved) {
            getHolder().newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.keyword.not-found"))
                       .highlightType(ProblemHighlightType.ERROR)
                       .range(keywordCallName)
                       .create();
        }
    }
}
