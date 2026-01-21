package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.highlight;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ReassignedRobotVariableAnnotator extends AbstractRobotVariableAnnotator {

    @Override
    protected void evaluateAnnotation(@NotNull RobotVariable variable) {
        RobotVariableBodyId variableBodyId = RobotPsiImplUtil.getVariableBodyId(variable);
        if (variableBodyId != null && ((PsiPolyVariantReference) variableBodyId.getReference()).multiResolve(false).length > 0) {
            ResolveResult[] resolveResults = ((PsiPolyVariantReference) variableBodyId.getReference()).multiResolve(false);
            if (Arrays.stream(resolveResults)
                      .filter(ResolveResult::isValidResult)
                      .map(ResolveResult::getElement)
                      .filter(result -> result instanceof RobotVariableDefinition)
                      .count() > 1) {
                TextAttributes base = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(RobotHighlighter.VARIABLE);
                TextAttributes merged = base.clone();
                merged.setEffectType(EffectType.LINE_UNDERSCORE);
                getHolder().newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                      .textAttributes(RobotHighlighter.REASSIGNED_VARIABLE)
                      .tooltip(RobotBundle.message("annotation.variable.reassigned"))
                      .range(variable)
                      .create();
            }
        }
    }
}
