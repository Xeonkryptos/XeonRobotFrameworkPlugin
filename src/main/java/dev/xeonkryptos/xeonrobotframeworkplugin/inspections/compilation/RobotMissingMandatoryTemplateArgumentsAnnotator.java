package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotMissingMandatoryTemplateArgumentsAnnotator extends RobotAnnotator {

    @Override
    public void visitTemplateArguments(@NotNull RobotTemplateArguments templateArguments) {
        Project project = templateArguments.getProject();
        KeywordUtil keywordUtil = KeywordUtil.getInstance(project);
        RobotKeywordCall keywordCall = keywordUtil.findTemplateKeywordCall(templateArguments);
        if (keywordCall != null) {
            Collection<String> missingRequiredParameters = templateArguments.computeMissingRequiredParameters();
            if (!missingRequiredParameters.isEmpty()) {
                getHolder().newAnnotation(HighlightSeverity.ERROR,
                                     RobotBundle.message("annotation.template.arguments.missing-required-parameters",
                                                         String.join(", ", missingRequiredParameters)))
                      .range(templateArguments)
                      .highlightType(ProblemHighlightType.GENERIC_ERROR)
                      .withFix(new InsertMissingMandatoryKeywordParametersQuickFix(missingRequiredParameters))
                      .create();
            }
        }
    }
}
