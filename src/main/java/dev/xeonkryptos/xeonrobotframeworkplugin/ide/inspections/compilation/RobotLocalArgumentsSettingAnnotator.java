package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

public class RobotLocalArgumentsSettingAnnotator implements Annotator, DumbAware {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotLocalArgumentsSetting argumentsSetting)) {
            return;
        }
        boolean keywordOnlyMarkerFound = false;
        for (RobotVariableDefinition variableDefinition : argumentsSetting.getVariableDefinitionList()) {
            String name = variableDefinition.getName();
            if (name == null) {
                if (!keywordOnlyMarkerFound) {
                    keywordOnlyMarkerFound = true;
                } else {
                    holder.newAnnotation(HighlightSeverity.ERROR,
                                         RobotBundle.getMessage("annotation.user-keyword.settings.argument.keyword-only-marker-more-than-once"))
                          .highlightType(ProblemHighlightType.GENERIC_ERROR)
                          .range(variableDefinition)
                          .withFix(new RemoveMoreThanOnceDefinedKeywordOnlyMarkersQuickFix())
                          .create();
                }
            }
        }
    }
}
