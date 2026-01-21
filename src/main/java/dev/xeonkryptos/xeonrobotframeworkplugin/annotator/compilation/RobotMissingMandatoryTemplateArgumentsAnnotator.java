package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotMissingMandatoryTemplateArgumentsAnnotator extends RobotAnnotator {

    @Override
    public void visitTemplateArguments(@NotNull RobotTemplateArguments templateArguments) {
        RobotKeywordCall keywordCall = KeywordUtil.findTemplateKeywordCall(templateArguments);
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
