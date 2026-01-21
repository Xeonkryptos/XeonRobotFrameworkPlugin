package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterMandatory;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterOptional;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class RobotLocalArgumentsSettingAnnotator extends RobotAnnotator {

    @Override
    public void visitLocalArgumentsSetting(@NotNull RobotLocalArgumentsSetting argumentsSetting) {
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
                        getHolder().newAnnotation(HighlightSeverity.ERROR,
                                                  RobotBundle.message("annotation.user-keyword.settings.argument.keyword-only-marker-more-than-once"))
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
                getHolder().newAnnotation(HighlightSeverity.ERROR,
                                          RobotBundle.message("annotation.user-keyword.settings.argument.variable-defined-after-keyword-only-marker"))
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

    private static class RemoveMoreThanOnceDefinedKeywordOnlyMarkersQuickFix extends PsiElementBaseIntentionAction {

        public RemoveMoreThanOnceDefinedKeywordOnlyMarkersQuickFix() {
            setText(RobotBundle.message("intention.family.remove.illegal-arguments-setting.keyword-only-markers.text"));
        }

        @Override
        public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
            RobotVariableDefinition variableDefinition = PsiTreeUtil.getParentOfType(element, RobotVariableDefinition.class);
            if (variableDefinition != null) {
                variableDefinition.delete();
            }
        }

        @Override
        public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
            if (PsiTreeUtil.getParentOfType(element, RobotLocalArgumentsSetting.class) != null) {
                RobotVariableDefinition variableDefinition = PsiTreeUtil.getParentOfType(element, RobotVariableDefinition.class);
                return variableDefinition != null && variableDefinition.getName() == null;
            }
            return false;
        }

        @NotNull
        @Override
        @IntentionFamilyName
        public String getFamilyName() {
            return RobotBundle.message("intention.family.remove.illegal-arguments-setting.keyword-only-markers.name");
        }
    }
}
