package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionId;
import org.jetbrains.annotations.NotNull;

public class RobotUnusedVariableAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element.isValid() && element instanceof VariableDefinitionId variableDefinitionId) {
            if (isUnused(variableDefinitionId)) {
                holder.newAnnotation(HighlightSeverity.WARNING, RobotBundle.getMessage("annotation.variable.unused"))
                      .textAttributes(CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES)
                      .highlightType(ProblemHighlightType.LIKE_UNUSED_SYMBOL)
                      .range(variableDefinitionId)
                      .withFix(new RemoveUnusedVariableDefinitionIntentAction(variableDefinitionId))
                      .create();
            }
        }
    }

    private boolean isUnused(VariableDefinitionId variableDefinitionId) {
        return ReferencesSearch.search(variableDefinitionId).findFirst() == null;
    }
}
