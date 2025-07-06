package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import org.jetbrains.annotations.NotNull;

class SettingsKeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        RobotSection section = CompletionProviderUtils.getSection(position);
        if (section instanceof RobotSettingsSection) {
            CompletionProviderUtils.addSyntaxLookup(RobotTypes.GLOBAL_SETTING_STATEMENT, result);
            CompletionProviderUtils.addSyntaxLookup(RobotKeywordProvider.IMPORT, result);
        }
    }
}
