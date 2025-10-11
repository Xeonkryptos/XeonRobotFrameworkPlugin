package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotMissingMandatoryTemplateArgumentsAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotTemplateArguments templateArguments)) {
            return;
        }

        Project project = templateArguments.getProject();
        KeywordUtil keywordUtil = KeywordUtil.getInstance(project);
        RobotKeywordCall keywordCall = keywordUtil.findTemplateKeywordCall(templateArguments);
        if (keywordCall != null) {
            Collection<String> missingRequiredParameters = templateArguments.computeMissingRequiredParameters();
            if (!missingRequiredParameters.isEmpty()) {
                holder.newAnnotation(HighlightSeverity.ERROR,
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
