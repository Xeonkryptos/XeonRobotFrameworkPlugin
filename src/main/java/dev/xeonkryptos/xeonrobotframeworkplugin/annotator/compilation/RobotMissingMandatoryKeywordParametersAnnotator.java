package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateStatementsGlobalSetting;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotMissingMandatoryKeywordParametersAnnotator extends RobotAnnotator {

    @Override
    public void visitKeywordCallName(@NotNull RobotKeywordCallName robotKeywordCallName) {
        RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(robotKeywordCallName, RobotKeywordCall.class);
        if (keywordCall != null && !keywordCall.allRequiredParametersArePresent()) {
            RobotElement ignoringParameterCheckParent = PsiTreeUtil.getParentOfType(keywordCall,
                                                                                    true,
                                                                                    RobotLocalSetting.class,
                                                                                    RobotTemplateStatementsGlobalSetting.class);
            if (ignoringParameterCheckParent == null) {
                Collection<String> missingParameters = keywordCall.computeMissingRequiredParameters();
                String missingRequiredParameters = String.join(", ", missingParameters);
                getHolder().newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.keyword.parameters.missing", missingRequiredParameters))
                           .highlightType(ProblemHighlightType.GENERIC_ERROR)
                           .range(robotKeywordCallName)
                           .withFix(new InsertMissingMandatoryKeywordParametersQuickFix(missingParameters))
                           .create();
            }
        }
    }
}
