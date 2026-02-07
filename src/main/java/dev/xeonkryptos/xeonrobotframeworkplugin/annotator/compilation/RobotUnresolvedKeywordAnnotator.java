package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import org.jetbrains.annotations.NotNull;

public class RobotUnresolvedKeywordAnnotator extends RobotAnnotator {

    @Override
    public void visitKeywordCallName(@NotNull RobotKeywordCallName keywordCallName) {
        PsiPolyVariantReference reference = (PsiPolyVariantReference) keywordCallName.getReference();
        ResolveResult[] resolveResults = reference.multiResolve(false);
        if (resolveResults.length == 0) {
            getHolder().newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.keyword.not-found"))
                       .highlightType(ProblemHighlightType.ERROR)
                       .range(keywordCallName)
                       .create();
        }
    }
}
