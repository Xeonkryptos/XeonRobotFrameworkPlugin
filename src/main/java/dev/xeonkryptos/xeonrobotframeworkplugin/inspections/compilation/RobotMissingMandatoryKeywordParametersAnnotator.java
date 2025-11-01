package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateStatementsGlobalSetting;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotMissingMandatoryKeywordParametersAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotKeywordCallName robotKeywordCallName)) {
            return;
        }

        RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(robotKeywordCallName, RobotKeywordCall.class);
        if (keywordCall != null && !keywordCall.allRequiredParametersArePresent()) {
            RobotElement ignoringParameterCheckParent = PsiTreeUtil.getParentOfType(keywordCall,
                                                                                    true,
                                                                                    RobotLocalSetting.class,
                                                                                    RobotTemplateStatementsGlobalSetting.class);
            if (ignoringParameterCheckParent == null) {
                Collection<String> missingParameters = keywordCall.computeMissingRequiredParameters();
                String missingRequiredParameters = String.join(", ", missingParameters);
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.keyword.parameters.missing", missingRequiredParameters))
                      .highlightType(ProblemHighlightType.GENERIC_ERROR)
                      .range(element)
                      .withFix(new InsertMissingMandatoryKeywordParametersQuickFix(missingParameters))
                      .create();
            }
        }
    }
}
