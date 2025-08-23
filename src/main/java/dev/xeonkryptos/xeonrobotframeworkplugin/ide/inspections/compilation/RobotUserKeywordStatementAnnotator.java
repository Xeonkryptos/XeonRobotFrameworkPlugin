package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RobotUserKeywordStatementAnnotator implements Annotator, DumbAware {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotUserKeywordStatement userKeywordStatement)) {
            return;
        }

        List<RobotLocalArgumentsSetting> argumentsSettings = userKeywordStatement.getLocalArgumentsSettingList();
        if (argumentsSettings.size() >= 2) {
            for (int i = 1; i < argumentsSettings.size(); i++) {
                PsiElement argument = argumentsSettings.get(i);
                holder.newAnnotation(HighlightSeverity.ERROR, RobotBundle.getMessage("annotation.user-keyword.settings.argument.defined-more-than-once"))
                      .highlightType(ProblemHighlightType.GENERIC_ERROR)
                      .range(argument)
                      .withFix(new RemoveMoreThanOnceDefinedLocalArgumentsSettingQuickFix())
                      .create();
            }
        }
    }
}
