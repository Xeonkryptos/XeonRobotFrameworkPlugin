package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RobotUserKeywordStatementAnnotator extends RobotAnnotator {

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement userKeywordStatement) {
        List<RobotLocalArgumentsSetting> argumentsSettings = userKeywordStatement.getLocalArgumentsSettingList();
        if (argumentsSettings.size() >= 2) {
            for (int i = 1; i < argumentsSettings.size(); i++) {
                PsiElement argument = argumentsSettings.get(i);
                getHolder().newAnnotation(HighlightSeverity.ERROR, RobotBundle.message("annotation.user-keyword.settings.argument.defined-more-than-once"))
                           .highlightType(ProblemHighlightType.GENERIC_ERROR)
                           .range(argument)
                           .withFix(new RemoveMoreThanOnceDefinedLocalArgumentsSettingQuickFix())
                           .create();
            }
        }
    }
}
