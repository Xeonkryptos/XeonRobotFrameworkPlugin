package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ReassignedRobotVariableAnnotator extends AbstractRobotVariableAnnotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (isEvaluatable(element)) {
            RobotVariable variable = (RobotVariable) element;
            RobotVariableBodyId variableBodyId = RobotPsiImplUtil.getVariableBodyId(variable);
            if (variableBodyId != null && ((PsiPolyVariantReference) variableBodyId.getReference()).multiResolve(false).length > 0) {
                ResolveResult[] resolveResults = ((PsiPolyVariantReference) variableBodyId.getReference()).multiResolve(false);
                if (Arrays.stream(resolveResults)
                          .filter(ResolveResult::isValidResult)
                          .map(ResolveResult::getElement)
                          .filter(result -> result instanceof RobotVariableDefinition)
                          .count() > 1) {
                    holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                          .textAttributes(DefaultLanguageHighlighterColors.REASSIGNED_LOCAL_VARIABLE)
                          .tooltip(RobotBundle.getMessage("annotation.variable.reassigned"))
                          .range(variable)
                          .create();
                }
            }
        }

    }
}
