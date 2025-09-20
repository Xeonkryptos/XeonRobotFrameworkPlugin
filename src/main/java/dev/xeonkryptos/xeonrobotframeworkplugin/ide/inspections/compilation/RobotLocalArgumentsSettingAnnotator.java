package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterMandatory;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterOptional;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class RobotLocalArgumentsSettingAnnotator implements Annotator, DumbAware {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotLocalArgumentsSetting argumentsSetting)) {
            return;
        }
        boolean keywordOnlyMarkerFound = false;
        for (RobotLocalArgumentsSettingParameter localArgumentsSettingParameter : argumentsSetting.getLocalArgumentsSettingParameterList()) {
            RobotLocalArgumentsSettingParameterMandatory parameterMandatory = localArgumentsSettingParameter.getLocalArgumentsSettingParameterMandatory();
            if (parameterMandatory != null) {
                RobotVariableDefinition variableDefinition = parameterMandatory.getVariableDefinition();
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

        if (keywordOnlyMarkerFound) {
            RobotLocalArgumentsSettingArgumentVisitor visitor = new RobotLocalArgumentsSettingArgumentVisitor();
            argumentsSetting.acceptChildren(visitor);
            for (PsiElement invalidVariableDefinition : visitor.invalidVariableDefinitions) {
                holder.newAnnotation(HighlightSeverity.ERROR,
                                     RobotBundle.getMessage("annotation.user-keyword.settings.argument.variable-defined-after-keyword-only-marker"))
                      .highlightType(ProblemHighlightType.GENERIC_ERROR)
                      .range(invalidVariableDefinition)
                      .create();
            }
        }
    }

    private static final class RobotLocalArgumentsSettingArgumentVisitor extends RecursiveRobotVisitor {

        private final Set<PsiElement> invalidVariableDefinitions = new LinkedHashSet<>();

        private boolean keywordOnlyMarkerFound = false;

        @Override
        public void visitLocalArgumentsSettingParameterOptional(@NotNull RobotLocalArgumentsSettingParameterOptional o) {
            // Avoid visiting any children of this kind. We will find other variable definitions and those we don't have to take a look at
        }

        @Override
        public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
            ProgressManager.checkCanceled();
            if (o.getName() == null) {
                keywordOnlyMarkerFound = true;
            } else if (keywordOnlyMarkerFound) {
                invalidVariableDefinitions.add(o);
            }
        }
    }
}
