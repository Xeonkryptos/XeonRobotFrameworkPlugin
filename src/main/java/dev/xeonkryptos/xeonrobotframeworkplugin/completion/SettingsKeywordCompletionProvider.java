package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import org.jetbrains.annotations.NotNull;

class SettingsKeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Project project = parameters.getPosition().getProject();
        CompletionProviderUtils.addSyntaxLookup(RobotKeywordProvider.GLOBAL_SETTING_STATEMENT, result, project);
        CompletionProviderUtils.addSyntaxLookup(RobotKeywordProvider.IMPORT, result, project);
    }
}
